package com.evcc.evcc.service;

import com.evcc.evcc.entity.TelematicsEvent;
import com.evcc.evcc.repository.TelematicsEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TelematicsEventService {
    @Autowired
    private TelematicsEventRepository telematicsEventRepository;

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