package com.evcc.poll.controller;

import com.evcc.poll.entity.Poll;
import com.evcc.poll.service.PollService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/polls")
public class PollController {
    @Autowired
    private PollService pollService;

    @GetMapping
    public List<Poll> getAllPolls() {
        return pollService.getAllPolls();
    }

    @PostMapping
    public Poll savePoll(@RequestBody Poll poll) {
        return pollService.savePoll(poll);
    }

    @GetMapping("/{id}")
    public Poll getPollById(@PathVariable UUID id) {
        return pollService.getPollById(id);
    }
}
