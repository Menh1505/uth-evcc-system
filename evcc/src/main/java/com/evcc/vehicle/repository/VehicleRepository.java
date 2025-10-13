package com.evcc.vehicle.repository;

import com.evcc.vehicle.entity.Vehicle;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
}
