package com.geotrip.authservice.repositories;


import com.geotrip.entityservice.models.Driver;
import com.geotrip.entityservice.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID>, UserFinder {
    Optional<Driver> findByEmailAndRole(String email, Role role);

    Optional<Driver> findByEmail(String email);
}
