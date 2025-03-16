package com.geotrip.authservice.services;

import com.geotrip.authservice.dtos.LoginRequestDto;
import com.geotrip.authservice.dtos.RegisterDriverRequestDto;
import com.geotrip.authservice.dtos.RegisterPassengerRequestDto;
import com.geotrip.authservice.dtos.UserDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    String registerDriver(RegisterDriverRequestDto registerDriverRequestDto, HttpServletResponse httpServletResponse);

    String registerPassenger(RegisterPassengerRequestDto registerPassengerRequestDto, HttpServletResponse httpServletResponse);

    String authenticateUser(LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse);

    String unauthenticateUser(HttpServletResponse httpServletResponse);

    UserDto validateToken(String token);

}
