package com.evcc.vehicle.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.evcc.vehicle.entity.TelematicsEvent;
import com.evcc.vehicle.repository.TelematicsEventRepository;




@Service
public class TelematicsEventService {
    private final TelematicsEventRepository telematicsEventRepository;

    public TelematicsEventService(TelematicsEventRepository telematicsEventRepository) {
        this.telematicsEventRepository = telematicsEventRepository;
    }

    public List<TelematicsEvent> getAllTelematicsEvents() {
        return telematicsEventRepository.findAll();
    }

    public TelematicsEvent saveTelematicsEvent(TelematicsEvent telematicsEvent) {
        return telematicsEventRepository.save(telematicsEvent);
    }

    public TelematicsEvent getTelematicsEventById(UUID id) {
        return telematicsEventRepository.findById(id).orElse(null);
    }
}
