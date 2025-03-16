package com.geotrip.authservice.services;

import com.geotrip.authservice.dtos.LoginRequestDto;
import com.geotrip.authservice.dtos.RegisterDriverRequestDto;
import com.geotrip.authservice.dtos.RegisterPassengerRequestDto;
import com.geotrip.authservice.dtos.UserDto;
import com.geotrip.authservice.repositories.DriverRepository;
import com.geotrip.authservice.repositories.PassengerRepository;
import com.geotrip.authservice.repositories.UserFinder;
import com.geotrip.entityservice.models.*;
import com.geotrip.exceptionhandler.AppException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtService;
    private final List<UserFinder> userFinders;
    private final AuthenticationManager authenticationManager;

    private static final long EXPIRATION_TIME = 60 * 60 * 10;

    @Value("${env}")
    private String environment;

//    @PostConstruct
//    public void init() {
//        System.out.println("UserFinders in Auth service Injected: " + (userFinders != null ? userFinders.size() : "NULL"));
//    }


    @Transactional
    public String registerDriver(RegisterDriverRequestDto registerDriverRequestDto, HttpServletResponse httpServletResponse) {
        if(driverRepository.findByEmail(registerDriverRequestDto.getEmail()).isPresent()) {
            throw new AppException("Driver already exists", HttpStatus.BAD_REQUEST);
        }

        Driver driver = new Driver();
        driver.setName(registerDriverRequestDto.getName());
        driver.setEmail(registerDriverRequestDto.getEmail());
        driver.setPassword(passwordEncoder.encode(registerDriverRequestDto.getPassword()));
        driver.setPhoneNumber(registerDriverRequestDto.getPhoneNumber());
        driver.setLicenseNumber(registerDriverRequestDto.getLicenseNumber());
        driver.setAverageRating(0.0);
        driver.setActive(true);
        driver.setApprovalStatus(DriverApprovalStatus.APPROVED);
        driver.setRole(Role.ROLE_DRIVER);
        driver.setIsEmailVerified(true);
        driver.setIsPhoneNumberVerified(true);
        driver.setIsLicenseNumberVerified(true);

        //TODO: will later implement the phone number, email verification and driving licence verification logic (now default all true)
        //TODO: will later implement driver approval status logic (now default value is APPROVED)
        //TODO: will later implement driver active status logic (now default value is active = true)

        driverRepository.save(driver);

        return authenticateUser(
                LoginRequestDto.builder()
                        .email(registerDriverRequestDto.getEmail())
                        .password(registerDriverRequestDto.getPassword())
                        .build(),
                httpServletResponse
        );
    }


    @Transactional
    public String registerPassenger(RegisterPassengerRequestDto registerPassengerRequestDto, HttpServletResponse httpServletResponse) {
        if(passengerRepository.findByEmail(registerPassengerRequestDto.getEmail()).isPresent()) {
            throw new AppException("Passenger already exists", HttpStatus.BAD_REQUEST);
        }

        Passenger passenger = new Passenger();
        passenger.setName(registerPassengerRequestDto.getName());
        passenger.setEmail(registerPassengerRequestDto.getEmail());
        passenger.setPassword(passwordEncoder.encode(registerPassengerRequestDto.getPassword()));
        passenger.setPhoneNumber(registerPassengerRequestDto.getPhoneNumber());
        passenger.setAverageRating(0.0);
        passenger.setRole(Role.ROLE_PASSENGER);
        passenger.setIsEmailVerified(true);
        passenger.setIsPhoneNumberVerified(true);


        //TODO: will later implement phone number and email verification logic


        passengerRepository.save(passenger);

        return authenticateUser(
                LoginRequestDto.builder()
                        .email(registerPassengerRequestDto.getEmail())
                        .password(registerPassengerRequestDto.getPassword())
                        .build(),
                httpServletResponse
        );
    }



    public String authenticateUser(LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) {
//        return userFinders.stream()
//                .map(repo -> repo.findByEmail(loginRequestDto.getEmail()))
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .filter(user -> passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword()))
//                .findFirst()
//                .map(user -> jwtService.generateToken(user.getEmail(), user.getRole()))
//                .orElseThrow(() -> new AppException("Invalid Credentials", HttpStatus.UNAUTHORIZED));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );

        if(!authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("Invalid username or password");
        }

        String email = authentication.getName();
        Role role = Role.valueOf(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse(null));

        String authToken = jwtService.generateToken(email, role, EXPIRATION_TIME);

        ResponseCookie responseCookie = ResponseCookie.from("authToken", authToken)
                .httpOnly(true)
                .secure(!environment.equals("dev"))
                .path("/")
                .maxAge(EXPIRATION_TIME)
                .build();

        httpServletResponse.setHeader("Set-Cookie", responseCookie.toString());
        return authToken;
    }


    public String unauthenticateUser(HttpServletResponse httpServletResponse) {
        ResponseCookie responseCookie = ResponseCookie.from("authToken", "")
                .httpOnly(true)
                .secure(!environment.equals("dev"))
                .path("/")
                .maxAge(0)
                .build();

        httpServletResponse.setHeader("Set-Cookie", responseCookie.toString());
        return "Successfully removed authToken";
    }

    public UserDto validateToken(String token){
        System.out.println("Token: " + token);
        if(token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            if(!jwtService.isTokenExpired(jwtToken)) {
                String email = jwtService.extractEmail(jwtToken);
                Role role = jwtService.extractRole(jwtToken);
                User userDetail = userFinders.stream()
                        .map(repo -> repo.findByEmailAndRole(email, role))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                        .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

                return UserDto.builder()
                        .id(userDetail.getId())
                        .email(userDetail.getEmail())
                        .role(userDetail.getRole())
                        .build();
            }
        }
        throw new AppException("Invalid Token", HttpStatus.UNAUTHORIZED);
    }

}
