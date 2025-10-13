package com.evcc.booking.repository;

import com.evcc.booking.entity.Handover;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface HandoverRepository extends JpaRepository<Handover, UUID> {
}
