package com.geotrip.authservice.repositories;

import com.geotrip.entityservice.models.Role;
import com.geotrip.entityservice.models.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserFinder {
    Optional<? extends User> findByEmailAndRole(String email, Role role);

    Optional<? extends User> findByEmail(String email);
}
