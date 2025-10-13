package com.evcc.evcc.service;

import com.evcc.evcc.entity.PollOption;
import com.evcc.evcc.repository.PollOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PollOptionService {
    @Autowired
    private PollOptionRepository pollOptionRepository;

    public List<PollOption> getAllPollOptions() {
        return pollOptionRepository.findAll();
    }

    public PollOption savePollOption(PollOption pollOption) {
        return pollOptionRepository.save(pollOption);
    }

    public PollOption getPollOptionById(UUID id) {
        return pollOptionRepository.findById(id).orElse(null);
    }
}