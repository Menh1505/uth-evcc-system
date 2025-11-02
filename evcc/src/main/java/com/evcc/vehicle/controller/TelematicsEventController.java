package com.evcc.vehicle.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evcc.vehicle.entity.TelematicsEvent;
import com.evcc.vehicle.service.TelematicsEventService;

@RestController
@RequestMapping("/telematics-events")
public class TelematicsEventController {
    private final TelematicsEventService telematicsEventService;

    public TelematicsEventController(TelematicsEventService telematicsEventService) {
        this.telematicsEventService = telematicsEventService;
    }

    @GetMapping
    public List<TelematicsEvent> getAllTelematicsEvents() {
        return telematicsEventService.getAllTelematicsEvents();
    }

    @PostMapping
    public TelematicsEvent saveTelematicsEvent(@RequestBody TelematicsEvent telematicsEvent) {
        return telematicsEventService.saveTelematicsEvent(telematicsEvent);
    }

    @GetMapping("/{id}")
    public TelematicsEvent getTelematicsEventById(@PathVariable UUID id) {
        return telematicsEventService.getTelematicsEventById(id);
    }
}
