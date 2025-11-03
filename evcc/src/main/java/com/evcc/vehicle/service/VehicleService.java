package com.evcc.vehicle.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evcc.group.entity.Group;
import com.evcc.group.repository.GroupRepository;
import com.evcc.vehicle.dto.VehicleCreateRequest;
import com.evcc.vehicle.dto.VehicleResponse;
import com.evcc.vehicle.entity.Vehicle;
import com.evcc.vehicle.enums.VehicleStatus;
import com.evcc.vehicle.repository.VehicleRepository;

@Service
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final GroupRepository groupRepository;

    public VehicleService(VehicleRepository vehicleRepository, GroupRepository groupRepository) {
        this.vehicleRepository = vehicleRepository;
        this.groupRepository = groupRepository;
    }

    public VehicleResponse createVehicle(VehicleCreateRequest req) {
        if (vehicleRepository.existsByLicensePlate(req.getLicensePlate())) {
            throw new IllegalArgumentException("License plate already exists: " + req.getLicensePlate());
        }

        Group group = groupRepository.findById(req.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + req.getGroupId()));

        Vehicle v = Vehicle.builder()
                .name(req.getName())
                .licensePlate(req.getLicensePlate())
                .make(req.getMake())
                .model(req.getModel())
                .year(req.getYear())
                .group(group)
                .purchasePrice(req.getPurchasePrice())
                .purchaseDate(req.getPurchaseDate())
                .batteryCapacity(req.getBatteryCapacity())
                .initialOdometer(req.getInitialOdometer())
                .status(parseStatus(req.getStatus()))
                .build();

        Vehicle saved = vehicleRepository.save(v);
        return toResponse(saved);
    }

    public VehicleResponse getVehicleById(Long id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + id));
        return toResponse(v);
    }

    public List<VehicleResponse> listAll() {
        return vehicleRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<VehicleResponse> listByGroup(Long groupId) {
        Group g = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId));
        return vehicleRepository.findByGroup(g).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new IllegalArgumentException("Vehicle not found: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public VehicleResponse updateVehicle(Long id, VehicleCreateRequest req) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + id));

        if (req.getLicensePlate() != null && !req.getLicensePlate().equals(v.getLicensePlate())
                && vehicleRepository.existsByLicensePlate(req.getLicensePlate())) {
            throw new IllegalArgumentException("License plate already exists: " + req.getLicensePlate());
        }

        if (req.getGroupId() != null) {
            Group g = groupRepository.findById(req.getGroupId())
                    .orElseThrow(() -> new IllegalArgumentException("Group not found: " + req.getGroupId()));
            v.setGroup(g);
        }

        // update fields if present
        if (req.getName() != null) v.setName(req.getName());
        if (req.getLicensePlate() != null) v.setLicensePlate(req.getLicensePlate());
        if (req.getMake() != null) v.setMake(req.getMake());
        if (req.getModel() != null) v.setModel(req.getModel());
        if (req.getYear() != null) v.setYear(req.getYear());
        if (req.getPurchasePrice() != null) v.setPurchasePrice(req.getPurchasePrice());
        if (req.getPurchaseDate() != null) v.setPurchaseDate(req.getPurchaseDate());
        if (req.getBatteryCapacity() != null) v.setBatteryCapacity(req.getBatteryCapacity());
        if (req.getInitialOdometer() != null) v.setInitialOdometer(req.getInitialOdometer());
        if (req.getStatus() != null) v.setStatus(parseStatus(req.getStatus()));

        Vehicle saved = vehicleRepository.save(v);
        return toResponse(saved);
    }

    private VehicleStatus parseStatus(String s) {
        if (s == null) return VehicleStatus.AVAILABLE;
        try {
            return VehicleStatus.valueOf(s.toUpperCase());
        } catch (Exception ex) {
            return VehicleStatus.AVAILABLE;
        }
    }

    private VehicleResponse toResponse(Vehicle v) {
        VehicleResponse r = new VehicleResponse();
        r.setId(v.getId());
        r.setName(v.getName());
        r.setLicensePlate(v.getLicensePlate());
        r.setMake(v.getMake());
        r.setModel(v.getModel());
        r.setYear(v.getYear());
        r.setGroupId(Optional.ofNullable(v.getGroup()).map(Group::getId).orElse(null));
        r.setPurchasePrice(v.getPurchasePrice());
        r.setPurchaseDate(v.getPurchaseDate());
        r.setBatteryCapacity(v.getBatteryCapacity());
        r.setInitialOdometer(v.getInitialOdometer());
        r.setStatus(v.getStatus());
        r.setCreatedAt(v.getCreatedAt());
        r.setUpdatedAt(v.getUpdatedAt());
        return r;
    }
}
