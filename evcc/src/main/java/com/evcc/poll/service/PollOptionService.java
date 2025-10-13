package com.evcc.poll.service;

import com.evcc.poll.entity.PollOption;
import com.evcc.poll.repository.PollOptionRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




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
