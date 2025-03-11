package com.geotrip.authservice.services;

import com.geotrip.authservice.dtos.LoginRequestDto;
import com.geotrip.authservice.dtos.RegisterDriverRequestDto;
import com.geotrip.authservice.dtos.RegisterPassengerRequestDto;
import com.geotrip.authservice.dtos.UserDto;

public interface AuthService {

    String registerDriver(RegisterDriverRequestDto registerDriverRequestDto);

    String registerPassenger(RegisterPassengerRequestDto registerPassengerRequestDto);

    String authenticateUser(LoginRequestDto loginRequestDto);

    UserDto validateToken(String token);

}
