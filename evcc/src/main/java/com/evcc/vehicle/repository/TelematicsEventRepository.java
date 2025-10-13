package com.evcc.vehicle.repository;

import com.evcc.vehicle.entity.TelematicsEvent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface TelematicsEventRepository extends JpaRepository<TelematicsEvent, UUID> {
}
