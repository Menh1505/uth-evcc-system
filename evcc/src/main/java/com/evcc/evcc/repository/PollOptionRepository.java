package com.evcc.evcc.repository;

import com.evcc.evcc.entity.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PollOptionRepository extends JpaRepository<PollOption, UUID> {
}