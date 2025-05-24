package com.teacheragenda.service;

import com.teacheragenda.model.Location;
import com.teacheragenda.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Transactional
    public Location saveLocation(Location location) throws LocationNameExistsException {
        // Check for existing location with the same name (case-insensitive)
        Optional<Location> existingLocation = locationRepository.findByNameIgnoreCase(location.getName());
        if (existingLocation.isPresent()) {
            // If it's a different entity (i.e., not updating the same location)
            if (location.getId() == null || !location.getId().equals(existingLocation.get().getId())) {
                throw new LocationNameExistsException("A location with the name '" + location.getName() + "' already exists.");
            }
        }
        return locationRepository.save(location);
    }

    @Transactional
    public void deleteLocation(Long locationId) {
        // Consider checking if the location is in use by an Event before deleting
        // For now, simple deletion.
        locationRepository.deleteById(locationId);
    }

    @Transactional(readOnly = true)
    public List<Location> getAllLocations() {
        return locationRepository.findAllByOrderByNameAsc(); // Return sorted by name
    }

    @Transactional(readOnly = true)
    public Optional<Location> getLocationById(Long locationId) {
        return locationRepository.findById(locationId);
    }

    // Custom exception for duplicate location names
    public static class LocationNameExistsException extends Exception {
        public LocationNameExistsException(String message) {
            super(message);
        }
    }
}
