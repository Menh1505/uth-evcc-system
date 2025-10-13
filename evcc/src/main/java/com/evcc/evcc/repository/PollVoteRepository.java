package com.evcc.evcc.repository;

import com.evcc.evcc.entity.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PollVoteRepository extends JpaRepository<PollVote, UUID> {
}