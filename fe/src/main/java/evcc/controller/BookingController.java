package evcc.controller;

import evcc.dto.local.LocalBooking;
import evcc.dto.local.LocalContract;
import evcc.dto.response.UserLoginResponse;
import evcc.dto.response.UserProfileResponseDto;
import evcc.service.BookingLocalService;
import evcc.service.BookingLocalService.BookingUsageStats;
import evcc.service.BookingLocalService.BookingValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/bookings/local")
public class BookingController {

    @Autowired
    private BookingLocalService bookingLocalService;

    private static final String REDIRECT_AUTH_LOGIN = "redirect:/auth/login";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String SUCCESS_MESSAGE = "successMessage";

    /**
     * Show user's bookings
     */
    @GetMapping
    public String getUserBookings(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        UserLoginResponse currentUser = (UserLoginResponse) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Vui lòng đăng nhập để xem lịch đặt xe.");
            return REDIRECT_AUTH_LOGIN;
        }

        try {
            UUID userId = UUID.fromString(currentUser.getUserId());
            List<LocalBooking> bookings = bookingLocalService.getUserBookings(userId);

            model.addAttribute("bookings", bookings);
            model.addAttribute("title", "Lịch đặt xe của tôi - EVCC System");
            return "bookings/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Lỗi khi tải danh sách lịch đặt: " + e.getMessage());
            return "redirect:/";
        }
    }

    /**
     * Show booking creation form
     */
    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        UserLoginResponse currentUser = (UserLoginResponse) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Vui lòng đăng nhập để đặt lịch xe.");
            return REDIRECT_AUTH_LOGIN;
        }

        try {
            UUID userId = UUID.fromString(currentUser.getUserId());
            System.out.println("DEBUG - User ID: " + userId);
            List<LocalContract> availableVehicles = bookingLocalService.getUserAvailableVehicles(userId);
            System.out.println("DEBUG - Available vehicles count: " + availableVehicles.size());

            if (availableVehicles.isEmpty()) {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE,
                        "Bạn chưa tham gia hợp đồng nào có xe để đặt lịch.");
                return "redirect:/contracts";
            }

            model.addAttribute("availableVehicles", availableVehicles);
            model.addAttribute("title", "Đặt lịch xe mới - EVCC System");
            model.addAttribute("minDate", LocalDate.now().toString());
            return "bookings/create";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Lỗi khi tải form đặt lịch: " + e.getMessage());
            return "redirect:/bookings";
        }
    }

    /**
     * Create new booking
     */
    @PostMapping("/create")
    public String createBooking(@RequestParam Long vehicleId,
            @RequestParam String bookingDate,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String purpose,
            @RequestParam(required = false) String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserLoginResponse currentUser = (UserLoginResponse) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Vui lòng đăng nhập để đặt lịch xe.");
            return REDIRECT_AUTH_LOGIN;
        }

        try {
            UUID userId = UUID.fromString(currentUser.getUserId());
            LocalDate date = LocalDate.parse(bookingDate);
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            LocalBooking booking = bookingLocalService.createBooking(userId, vehicleId, date, start, end, purpose, notes);

            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE,
                    "Đặt lịch xe thành công! Mã đặt lịch: " + booking.getId());
            return "redirect:/bookings";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Lỗi khi đặt lịch: " + e.getMessage());
            return "redirect:/bookings/create";
        }
    }

    /**
     * Cancel booking
     */
    @PostMapping("/{bookingId}/cancel")
    public String cancelBooking(@PathVariable Long bookingId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserLoginResponse currentUser = (UserLoginResponse) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Vui lòng đăng nhập.");
            return REDIRECT_AUTH_LOGIN;
        }

        try {
            UUID userId = UUID.fromString(currentUser.getUserId());
            boolean success = bookingLocalService.cancelBooking(bookingId, userId);

            if (success) {
                redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Hủy lịch đặt xe thành công.");
            } else {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Không tìm thấy lịch đặt xe.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Lỗi khi hủy lịch: " + e.getMessage());
        }

        return "redirect:/bookings";
    }

    /**
     * Get usage statistics for a vehicle
     */
    @GetMapping("/usage-stats")
    @ResponseBody
    public BookingUsageStats getUserUsageStats(@RequestParam Long vehicleId,
            HttpSession session) {
        UserLoginResponse currentUser = (UserLoginResponse) session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new IllegalArgumentException("Không tìm thấy thông tin người dùng");
        }

        UUID userId = UUID.fromString(currentUser.getUserId());
        return bookingLocalService.getUserUsageStats(userId, vehicleId);
    }

    /**
     * Validate booking before submission
     */
    @PostMapping("/validate")
    @ResponseBody
    public BookingValidationResult validateBooking(@RequestParam Long vehicleId,
            @RequestParam String bookingDate,
            @RequestParam String startTime,
            @RequestParam String endTime,
            HttpSession session) {
        UserLoginResponse currentUser = (UserLoginResponse) session.getAttribute("currentUser");
        if (currentUser == null) {
            return BookingValidationResult.error("Vui lòng đăng nhập");
        }

        try {
            UUID userId = UUID.fromString(currentUser.getUserId());
            LocalDate date = LocalDate.parse(bookingDate);
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            return bookingLocalService.validateBooking(userId, vehicleId, date, start, end);
        } catch (Exception e) {
            return BookingValidationResult.error("Lỗi validation: " + e.getMessage());
        }
    }

    /**
     * Get vehicle bookings for calendar view
     */
    @GetMapping("/calendar")
    public String showCalendar(@RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) String date,
            Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        UserLoginResponse currentUser = (UserLoginResponse) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Vui lòng đăng nhập để xem lịch.");
            return REDIRECT_AUTH_LOGIN;
        }

        try {
            UUID userId = UUID.fromString(currentUser.getUserId());
            List<LocalContract> availableVehicles = bookingLocalService.getUserAvailableVehicles(userId);

            model.addAttribute("availableVehicles", availableVehicles);
            model.addAttribute("selectedVehicleId", vehicleId);
            model.addAttribute("selectedDate", date != null ? date : LocalDate.now().toString());
            model.addAttribute("title", "Lịch sử dụng xe - EVCC System");

            // If vehicle and date are selected, get bookings for that day
            if (vehicleId != null && date != null) {
                LocalDate selectedDate = LocalDate.parse(date);
                List<LocalBooking> dayBookings = bookingLocalService.getVehicleBookingsForDate(vehicleId, selectedDate);
                model.addAttribute("dayBookings", dayBookings);
            }

            return "bookings/calendar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Lỗi khi tải lịch: " + e.getMessage());
            return "redirect:/bookings";
        }
    }
}
