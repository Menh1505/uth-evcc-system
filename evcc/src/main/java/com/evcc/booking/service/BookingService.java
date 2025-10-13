package com.evcc.booking.service;

import com.evcc.booking.entity.Booking;
import com.evcc.booking.repository.BookingRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(UUID id) {
        return bookingRepository.findById(id).orElse(null);
    }
}
