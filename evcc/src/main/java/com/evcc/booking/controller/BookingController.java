package com.evcc.booking.controller;

import com.evcc.booking.entity.Booking;
import com.evcc.booking.service.BookingService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @PostMapping
    public Booking saveBooking(@RequestBody Booking booking) {
        return bookingService.saveBooking(booking);
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable UUID id) {
        return bookingService.getBookingById(id);
    }
}
