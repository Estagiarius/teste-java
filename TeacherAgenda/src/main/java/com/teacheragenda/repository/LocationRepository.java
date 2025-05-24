package com.teacheragenda.repository;

import com.teacheragenda.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    // Find a location by its name, ignoring case. Useful for checking duplicates.
    Optional<Location> findByNameIgnoreCase(String name);

    // Find all locations, ordered by name alphabetically.
    List<Location> findAllByOrderByNameAsc();

    // You can add other specific queries if needed, for example:
    // List<Location> findByAddressContainingIgnoreCase(String addressKeyword);
}
