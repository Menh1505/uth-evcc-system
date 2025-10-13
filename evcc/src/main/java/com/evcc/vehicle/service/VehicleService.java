package com.evcc.vehicle.service;

import com.evcc.vehicle.entity.Vehicle;
import com.evcc.vehicle.repository.VehicleRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Vehicle getVehicleById(UUID id) {
        return vehicleRepository.findById(id).orElse(null);
    }
}
