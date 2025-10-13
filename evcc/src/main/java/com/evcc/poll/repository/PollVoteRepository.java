package com.evcc.poll.repository;

import com.evcc.poll.entity.PollVote;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface PollVoteRepository extends JpaRepository<PollVote, UUID> {
}
