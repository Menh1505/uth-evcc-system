package com.evcc.evcc.service;

import com.evcc.evcc.entity.PollVote;
import com.evcc.evcc.repository.PollVoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PollVoteService {
    @Autowired
    private PollVoteRepository pollVoteRepository;

    public List<PollVote> getAllPollVotes() {
        return pollVoteRepository.findAll();
    }

    public PollVote savePollVote(PollVote pollVote) {
        return pollVoteRepository.save(pollVote);
    }

    public PollVote getPollVoteById(UUID id) {
        return pollVoteRepository.findById(id).orElse(null);
    }
}