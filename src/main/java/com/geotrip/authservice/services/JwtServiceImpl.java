package com.geotrip.authservice.services;

import com.geotrip.entityservice.models.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;


    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Role extractRole(String token){
        String roleString = extractClaim(token, claims -> claims.get("role", String.class));
        return Role.valueOf(roleString);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claimsResolver.apply(claims);
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }


    /**
     * Checks whether the given token has expired or not
     * @param token Bearer token passed in the Authorization header
     * @return true if the token has expired otherwise false
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).toInstant().isBefore(new Date().toInstant());
    }

    public String generateToken(String email, Role role, Long expirationTime) {
        return Jwts.builder()
                .subject(email)
                .claims(Map.of("role", role.name()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime * 1000))
                .signWith(getSignKey())
                .compact();
    }
}
