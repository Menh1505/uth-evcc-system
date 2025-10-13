package com.evcc.poll.repository;

import com.evcc.poll.entity.PollOption;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface PollOptionRepository extends JpaRepository<PollOption, UUID> {
}
