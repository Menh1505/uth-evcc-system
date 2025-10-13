package com.evcc.evcc.service;

import com.evcc.evcc.entity.Poll;
import com.evcc.evcc.repository.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PollService {
    @Autowired
    private PollRepository pollRepository;

    public List<Poll> getAllPolls() {
        return pollRepository.findAll();
    }

    public Poll savePoll(Poll poll) {
        return pollRepository.save(poll);
    }

    public Poll getPollById(UUID id) {
        return pollRepository.findById(id).orElse(null);
    }
}