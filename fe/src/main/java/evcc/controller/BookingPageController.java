package evcc.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import evcc.dto.booking.BookingCreateRequestDto;
import evcc.service.BookingService;

// @Controller - Temporarily disabled to avoid conflicts with new BookingController
// @RequestMapping("/bookings")
public class BookingPageController {

    @Autowired
    private BookingService bookingService;

    private static final String ERROR_ATTR = "error";
    private static final String SUCCESS_ATTR = "success";
    private static final String BOOKINGS_ATTR = "bookings";

    @GetMapping
    public String bookingList(Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            if (!isAuthenticated(session)) {
                redirectAttributes.addFlashAttribute(ERROR_ATTR, "Please login to view your bookings");
                return "redirect:/auth/login";
            }

            UUID userId = getCurrentUserId(session);

            // Get user bookings for current month by default
            LocalDate fromDate = LocalDate.now().withDayOfMonth(1);
            LocalDate toDate = fromDate.plusMonths(1).minusDays(1);

            List<Object> bookings = bookingService.getUserBookings(userId, fromDate, toDate);
            model.addAttribute(BOOKINGS_ATTR, bookings);
            return "bookings/list";
        } catch (Exception e) {
            model.addAttribute(ERROR_ATTR, "Unable to load bookings: " + e.getMessage());
            return "bookings/list";
        }
    }

    @GetMapping("/create")
    public String createBookingForm(Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            if (!isAuthenticated(session)) {
                redirectAttributes.addFlashAttribute(ERROR_ATTR, "Please login to create a booking");
                return "redirect:/auth/login";
            }

            model.addAttribute("booking", new BookingCreateRequestDto());
            return "bookings/create";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, "Unable to load booking form: " + e.getMessage());
            return "redirect:/bookings";
        }
    }

    @PostMapping("/create")
    public String createBooking(BookingCreateRequestDto booking,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            if (!isAuthenticated(session)) {
                redirectAttributes.addFlashAttribute(ERROR_ATTR, "Please login to create a booking");
                return "redirect:/auth/login";
            }

            if (result.hasErrors()) {
                return "bookings/create";
            }

            UUID userId = getCurrentUserId(session);
            booking.setUserId(userId);

            bookingService.createBooking(booking);

            redirectAttributes.addFlashAttribute(SUCCESS_ATTR, "Booking created successfully!");
            return "redirect:/bookings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, "Failed to create booking: " + e.getMessage());
            return "redirect:/bookings/create";
        }
    }

    @GetMapping("/{bookingId}")
    public String bookingDetail(@PathVariable Long bookingId,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            if (!isAuthenticated(session)) {
                redirectAttributes.addFlashAttribute(ERROR_ATTR, "Please login to view booking details");
                return "redirect:/auth/login";
            }

            Object booking = bookingService.getBookingById(bookingId);
            model.addAttribute("booking", booking);
            return "bookings/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, "Booking not found: " + e.getMessage());
            return "redirect:/bookings";
        }
    }

    @PostMapping("/{bookingId}/cancel")
    public String cancelBooking(@PathVariable Long bookingId,
            @RequestParam(defaultValue = "User cancelled") String reason,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            if (!isAuthenticated(session)) {
                redirectAttributes.addFlashAttribute(ERROR_ATTR, "Please login to cancel booking");
                return "redirect:/auth/login";
            }

            UUID userId = getCurrentUserId(session);
            bookingService.cancelBooking(bookingId, userId, reason);

            redirectAttributes.addFlashAttribute(SUCCESS_ATTR, "Booking cancelled successfully!");
            return "redirect:/bookings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, "Failed to cancel booking: " + e.getMessage());
            return "redirect:/bookings";
        }
    }

    @GetMapping("/calendar")
    public String showCalendar(Model model,
            HttpSession session,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) String date,
            @RequestParam(defaultValue = "week") String view,
            RedirectAttributes redirectAttributes) {
        try {
            if (!isAuthenticated(session)) {
                redirectAttributes.addFlashAttribute(ERROR_ATTR, "Please login to view calendar");
                return "redirect:/auth/login";
            }

            // Parse date parameter
            LocalDate selectedDate = LocalDate.now();
            if (date != null && !date.isEmpty()) {
                try {
                    selectedDate = LocalDate.parse(date);
                } catch (DateTimeParseException e) {
                    selectedDate = LocalDate.now();
                }
            }

            // Get calendar data based on view type
            Object calendarData = null;
            if (vehicleId != null) {
                switch (view.toLowerCase()) {
                    case "day":
                        calendarData = bookingService.getDayView(vehicleId, selectedDate);
                        break;
                    case "month":
                        calendarData = bookingService.getMonthView(vehicleId, selectedDate.getYear(), selectedDate.getMonthValue());
                        break;
                    default: // week
                        LocalDate weekStart = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() - 1);
                        calendarData = bookingService.getWeekView(vehicleId, weekStart);
                        break;
                }
            }

            model.addAttribute("calendarData", calendarData);
            model.addAttribute("selectedDate", selectedDate);
            model.addAttribute("selectedVehicle", vehicleId);
            model.addAttribute("currentView", view);

            return "bookings/calendar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, "Unable to load calendar: " + e.getMessage());
            return "redirect:/bookings";
        }
    }

    private UUID getCurrentUserId(HttpSession session) {
        // Get user ID from session based on existing pattern
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser != null) {
            // Assuming UserResponseDto has getId() method that returns String
            try {
                // This is a simplified approach - adjust based on your actual UserResponseDto structure
                if (currentUser instanceof java.util.Map) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> userMap = (java.util.Map<String, Object>) currentUser;
                    String userId = (String) userMap.get("id");
                    if (userId != null) {
                        return UUID.fromString(userId);
                    }
                }
            } catch (Exception e) {
                // Log error if needed
            }
        }
        throw new IllegalStateException("User not authenticated");
    }

    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }
}
