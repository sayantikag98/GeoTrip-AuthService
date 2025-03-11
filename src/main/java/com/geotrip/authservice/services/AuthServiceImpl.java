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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

//    @PostConstruct
//    public void init() {
//        System.out.println("UserFinders in Auth service Injected: " + (userFinders != null ? userFinders.size() : "NULL"));
//    }


    @Transactional
    public String registerDriver(RegisterDriverRequestDto registerDriverRequestDto) {
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
        driver.setActive(false);
        driver.setApprovalStatus(DriverApprovalStatus.PENDING);
        driver.setRole(Role.DRIVER);
        driver.setIsEmailVerified(false);
        driver.setIsPhoneNumberVerified(false);
        driver.setIsLicenseNumberVerified(false);

        driverRepository.save(driver);

        return jwtService.generateToken(driver.getEmail(), driver.getRole());
    }


    @Transactional
    public String registerPassenger(RegisterPassengerRequestDto registerPassengerRequestDto) {
        if(passengerRepository.findByEmail(registerPassengerRequestDto.getEmail()).isPresent()) {
            throw new AppException("Passenger already exists", HttpStatus.BAD_REQUEST);
        }

        Passenger passenger = new Passenger();
        passenger.setName(registerPassengerRequestDto.getName());
        passenger.setEmail(registerPassengerRequestDto.getEmail());
        passenger.setPassword(passwordEncoder.encode(registerPassengerRequestDto.getPassword()));
        passenger.setPhoneNumber(registerPassengerRequestDto.getPhoneNumber());
        passenger.setAverageRating(0.0);
        passenger.setRole(Role.PASSENGER);
        passenger.setIsEmailVerified(false);
        passenger.setIsPhoneNumberVerified(false);


        passengerRepository.save(passenger);

        return jwtService.generateToken(passenger.getEmail(), passenger.getRole());
    }



    public String authenticateUser(LoginRequestDto loginRequestDto) {
        return userFinders.stream()
                .map(repo -> repo.findByEmail(loginRequestDto.getEmail()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(user -> passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword()))
                .findFirst()
                .map(user -> jwtService.generateToken(user.getEmail(), user.getRole()))
                .orElseThrow(() -> new AppException("Invalid Credentials", HttpStatus.UNAUTHORIZED));
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
