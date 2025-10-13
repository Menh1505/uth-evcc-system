package com.evcc.evcc.controller;

import com.evcc.evcc.entity.PollOption;
import com.evcc.evcc.service.PollOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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