package com.geotrip.authservice.controllers;


import com.geotrip.authservice.dtos.LoginRequestDto;
import com.geotrip.authservice.dtos.RegisterDriverRequestDto;
import com.geotrip.authservice.dtos.RegisterPassengerRequestDto;
import com.geotrip.authservice.dtos.UserDto;
import com.geotrip.authservice.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/driver")
    public ResponseEntity<String> registerDriver(@RequestBody @Valid RegisterDriverRequestDto registerDriverRequestDto) {
        return ResponseEntity.ok(authService.registerDriver(registerDriverRequestDto));
    }

    @PostMapping("/register/passenger")
    public ResponseEntity<String> registerPassenger(@RequestBody @Valid RegisterPassengerRequestDto registerPassengerRequestDto) {
        return ResponseEntity.ok(authService.registerPassenger(registerPassengerRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequestDto));
    }

    @PostMapping("/validate")
    public ResponseEntity<UserDto> validateUser(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }
}
