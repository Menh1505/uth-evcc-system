package com.evcc.poll.controller;

import com.evcc.poll.entity.PollVote;
import com.evcc.poll.service.PollVoteService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




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
