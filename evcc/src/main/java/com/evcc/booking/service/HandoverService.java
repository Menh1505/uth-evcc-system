package com.evcc.booking.service;

import com.evcc.booking.entity.Handover;
import com.evcc.booking.repository.HandoverRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service
public class HandoverService {
    @Autowired
    private HandoverRepository handoverRepository;

    public List<Handover> getAllHandovers() {
        return handoverRepository.findAll();
    }

    public Handover saveHandover(Handover handover) {
        return handoverRepository.save(handover);
    }

    public Handover getHandoverById(UUID id) {
        return handoverRepository.findById(id).orElse(null);
    }
}
