package com.evcc.evcc.repository;

import com.evcc.evcc.entity.Handover;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface HandoverRepository extends JpaRepository<Handover, UUID> {
}