package com.evcc.evcc.controller;

import com.evcc.evcc.entity.PollVote;
import com.evcc.evcc.service.PollVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/poll-votes")
public class PollVoteController {
    @Autowired
    private PollVoteService pollVoteService;

    @GetMapping
    public List<PollVote> getAllPollVotes() {
        return pollVoteService.getAllPollVotes();
    }

    @PostMapping
    public PollVote savePollVote(@RequestBody PollVote pollVote) {
        return pollVoteService.savePollVote(pollVote);
    }

    @GetMapping("/{id}")
    public PollVote getPollVoteById(@PathVariable UUID id) {
        return pollVoteService.getPollVoteById(id);
    }
}