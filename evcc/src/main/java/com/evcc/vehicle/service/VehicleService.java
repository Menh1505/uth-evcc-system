package com.evcc.vehicle.service;

import com.evcc.vehicle.dto.VehicleCreateRequest;
import com.evcc.vehicle.dto.VehicleResponse;
import com.evcc.vehicle.entity.Vehicle;
import com.evcc.vehicle.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;
    
    // (Sẽ cần GroupRepository sau)
    // @Autowired
    // private GroupRepository groupRepository;

    /**
     * Tạo một xe mới (thường dùng cho Admin/Staff)
     */
    public VehicleResponse createVehicle(VehicleCreateRequest request) {
        // Kiểm tra xem biển số đã tồn tại chưa
        if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new IllegalArgumentException("Biển số xe " + request.getLicensePlate() + " đã tồn tại.");
        }
        
        // (Logic tìm Group sẽ ở đây)
        // Group ownerGroup = groupRepository.findById(request.getOwnerGroupId())
        //         .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm"));

        Vehicle vehicle = Vehicle.builder()
                .licensePlate(request.getLicensePlate())
                .model(request.getModel())
                .vinNumber(request.getVinNumber())
                .modelYear(request.getModelYear())
                .batteryCapacityKWh(request.getBatteryCapacityKWh())
                // .ownerGroup(ownerGroup) // Gán nhóm sở hữu
                .build();
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapToResponse(savedVehicle);
    }

    /**
     * Lấy thông tin xe bằng ID
     */
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe với ID: " + id));
        return mapToResponse(vehicle);
    }

    /**
     * Lấy tất cả xe
     */
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // --- Hàm Helper ---

    /**
     * Hàm chuyển đổi (map) từ Entity sang DTO Response
     */
    private VehicleResponse mapToResponse(Vehicle vehicle) {
        VehicleResponse res = new VehicleResponse();
        res.setId(vehicle.getId());
        res.setLicensePlate(vehicle.getLicensePlate());
        res.setModel(vehicle.getModel());
        res.setVinNumber(vehicle.getVinNumber());
        res.setModelYear(vehicle.getModelYear());
        res.setBatteryCapacityKWh(vehicle.getBatteryCapacityKWh());
        // if (vehicle.getOwnerGroup() != null) {
        //     res.setOwnerGroupId(vehicle.getOwnerGroup().getId());
        // }
        return res;
    }
}