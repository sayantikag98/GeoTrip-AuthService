package com.geotrip.authservice.services;


import com.geotrip.authservice.repositories.UserFinder;
import com.geotrip.entityservice.models.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final List<UserFinder> userFinders;
    private Role role;

//    @PostConstruct
//    public void init() {
//        System.out.println("UserFinders Injected: " + (userFinders != null ? userFinders.size() : "NULL"));
//    }

    public UserDetails loadUserByUsernameHelper(String username, String role) throws UsernameNotFoundException {
        this.role = Role.valueOf(role);
        return loadUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //here username is the user email
        return userFinders.stream()
                .map(repositories -> repositories.findByEmailAndRole(username, role))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User with the given email " +username+" and role " +role+" not found"));
    }
}
