package com.evcc.vehicle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.evcc.group.entity.Group;
import com.evcc.vehicle.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByLicensePlate(String licensePlate);

    List<Vehicle> findByGroup(Group group);

    /**
     * Tìm xe chưa được gán cho bất kỳ hợp đồng ACTIVE nào
     */
    @Query("SELECT v FROM Vehicle v WHERE v NOT IN "
            + "(SELECT c.vehicle FROM Contract c WHERE c.status = 'ACTIVE' AND c.vehicle IS NOT NULL)")
    List<Vehicle> findAvailableVehicles();
}
