package com.evcc.evcc.controller;

import com.evcc.evcc.entity.TelematicsEvent;
import com.evcc.evcc.service.TelematicsEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/telematics-events")
public class TelematicsEventController {
    @Autowired
    private TelematicsEventService telematicsEventService;

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