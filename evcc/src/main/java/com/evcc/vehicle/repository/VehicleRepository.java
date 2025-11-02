package com.evcc.vehicle.repository;

import com.evcc.vehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    // Tìm xe bằng biển số
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    
    // Kiểm tra xem biển số đã tồn tại chưa
    boolean existsByLicensePlate(String licensePlate);
}