package com.evcc.vehicle.controller;

import com.evcc.vehicle.entity.Vehicle;
import com.evcc.vehicle.service.VehicleService;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @PostMapping
    public Vehicle saveVehicle(@RequestBody Vehicle vehicle) {
        return vehicleService.saveVehicle(vehicle);
    }

    @GetMapping("/{id}")
    public Vehicle getVehicleById(@PathVariable UUID id) {
        return vehicleService.getVehicleById(id);
    }
}
