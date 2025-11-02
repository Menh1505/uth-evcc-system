package com.evcc.vehicle.controller;

import com.evcc.vehicle.dto.VehicleCreateRequest;
import com.evcc.vehicle.dto.VehicleResponse;
import com.evcc.vehicle.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    /**
     * API tạo xe mới
     * (Sau này sẽ thêm @PreAuthorize("hasRole('ADMIN')")
     */
    @PostMapping("/")
    public ResponseEntity<VehicleResponse> createVehicle(@RequestBody VehicleCreateRequest request) {
        VehicleResponse newVehicle = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newVehicle);
    }

    /**
     * API lấy thông tin chi tiết 1 xe
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    /**
     * API lấy danh sách tất cả xe
     */
    @GetMapping("/")
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }
}