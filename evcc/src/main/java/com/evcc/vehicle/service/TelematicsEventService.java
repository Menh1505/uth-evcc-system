package com.evcc.vehicle.service;

import com.evcc.vehicle.entity.TelematicsEvent;
import com.evcc.vehicle.repository.TelematicsEventRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




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
