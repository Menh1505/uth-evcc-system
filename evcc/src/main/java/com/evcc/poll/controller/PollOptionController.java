package com.evcc.poll.controller;

import com.evcc.poll.entity.PollOption;
import com.evcc.poll.service.PollOptionService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/poll-options")
public class PollOptionController {
    @Autowired
    private PollOptionService pollOptionService;

    @GetMapping
    public List<PollOption> getAllPollOptions() {
        return pollOptionService.getAllPollOptions();
    }

    @PostMapping
    public PollOption savePollOption(@RequestBody PollOption pollOption) {
        return pollOptionService.savePollOption(pollOption);
    }

    @GetMapping("/{id}")
    public PollOption getPollOptionById(@PathVariable UUID id) {
        return pollOptionService.getPollOptionById(id);
    }
}
