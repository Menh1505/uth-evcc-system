package com.evcc.poll.service;

import com.evcc.poll.entity.PollVote;
import com.evcc.poll.repository.PollVoteRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




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
