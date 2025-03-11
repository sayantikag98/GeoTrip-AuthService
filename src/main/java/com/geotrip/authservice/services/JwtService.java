package com.geotrip.authservice.services;

import com.geotrip.entityservice.models.Role;

import java.util.Date;

public interface JwtService {

    String extractEmail(String token);

    Date extractExpiration(String token);

    Role extractRole(String token);

    boolean isTokenExpired(String token);

    String generateToken(String email, Role role);
}

