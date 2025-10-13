package com.evcc.poll.service;

import com.evcc.poll.entity.Poll;
import com.evcc.poll.repository.PollRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




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
