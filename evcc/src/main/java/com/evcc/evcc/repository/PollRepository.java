package com.evcc.evcc.repository;

import com.evcc.evcc.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PollRepository extends JpaRepository<Poll, UUID> {
}