package com.evcc.booking.repository;

import com.evcc.booking.entity.Booking;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface BookingRepository extends JpaRepository<Booking, UUID> {
}
