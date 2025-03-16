package com.geotrip.authservice.controllers;


import com.geotrip.authservice.dtos.LoginRequestDto;
import com.geotrip.authservice.dtos.RegisterDriverRequestDto;
import com.geotrip.authservice.dtos.RegisterPassengerRequestDto;
import com.geotrip.authservice.dtos.UserDto;
import com.geotrip.authservice.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<String> registerDriver(@RequestBody @Valid RegisterDriverRequestDto registerDriverRequestDto, HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(authService.registerDriver(registerDriverRequestDto, httpServletResponse));
    }

    @PostMapping("/register/passenger")
    public ResponseEntity<String> registerPassenger(@RequestBody @Valid RegisterPassengerRequestDto registerPassengerRequestDto, HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(authService.registerPassenger(registerPassengerRequestDto, httpServletResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequestDto, httpServletResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(authService.unauthenticateUser(httpServletResponse));
    }

    @PostMapping("/validate")
    public ResponseEntity<UserDto> validateUser(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }

    //TODO: to be removed
    @GetMapping("/websocket")
    public ResponseEntity<String> setCookieForWebSocket(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Set-Cookie", "authToken=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJrYXJ0aGlrQGdtYWlsLmNvbSIsInJvbGUiOiJST0xFX0RSSVZFUiIsImlhdCI6MTc0MjEzODA3NywiZXhwIjoxNzQyMTc0MDc3fQ.gKVljEnmpzzAi6BT8ger183Lycmuu8jV4gdwdAdWOo12H38DHVltGNBgBDYGZqJrq-RDbYPSODGyjdRbNH-NLg; Path=/; HttpOnly;");
        return ResponseEntity.ok("Success");
    }
}
