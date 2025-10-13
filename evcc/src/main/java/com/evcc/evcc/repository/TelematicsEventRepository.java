package com.evcc.evcc.repository;

import com.evcc.evcc.entity.TelematicsEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TelematicsEventRepository extends JpaRepository<TelematicsEvent, UUID> {
}