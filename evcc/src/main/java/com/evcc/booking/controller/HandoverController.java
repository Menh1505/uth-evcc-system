package com.evcc.booking.controller;

import com.evcc.booking.entity.Handover;
import com.evcc.booking.service.HandoverService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/handovers")
public class HandoverController {
    @Autowired
    private HandoverService handoverService;

    @GetMapping
    public List<Handover> getAllHandovers() {
        return handoverService.getAllHandovers();
    }

    @PostMapping
    public Handover saveHandover(@RequestBody Handover handover) {
        return handoverService.saveHandover(handover);
    }

    @GetMapping("/{id}")
    public Handover getHandoverById(@PathVariable UUID id) {
        return handoverService.getHandoverById(id);
    }
}
