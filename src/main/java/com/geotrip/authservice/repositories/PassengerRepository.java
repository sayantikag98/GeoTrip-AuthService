package com.geotrip.authservice.repositories;

import com.geotrip.entityservice.models.Passenger;
import com.geotrip.entityservice.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, UUID>, UserFinder {
    Optional<Passenger> findByEmailAndRole(String email, Role role);

    Optional<Passenger> findByEmail(String email);
}
