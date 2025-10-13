package com.evcc.poll.repository;

import com.evcc.poll.entity.Poll;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface PollRepository extends JpaRepository<Poll, UUID> {
}
