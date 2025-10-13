package com.evcc.vehicle.controller;

import com.evcc.vehicle.entity.TelematicsEvent;
import com.evcc.vehicle.service.TelematicsEventService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
