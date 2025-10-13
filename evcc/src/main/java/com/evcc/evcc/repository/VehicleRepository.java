package com.evcc.evcc.repository;

import com.evcc.evcc.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
}