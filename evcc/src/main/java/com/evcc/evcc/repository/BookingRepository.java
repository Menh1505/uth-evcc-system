package com.evcc.evcc.repository;

import com.evcc.evcc.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
}