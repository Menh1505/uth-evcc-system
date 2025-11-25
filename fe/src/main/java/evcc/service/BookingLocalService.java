package evcc.service;

import evcc.dto.local.LocalBooking;
import evcc.dto.local.LocalContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class BookingLocalService {

    private final Map<Long, LocalBooking> bookings = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Autowired
    private ContractLocalService contractLocalService;

    @Autowired
    private UserLocalService userLocalService;

    public static class BookingUsageStats {

        private final UUID userId;
        private final String username;
        private final BigDecimal ownershipPercentage;
        private final int hoursUsedThisWeek;
        private final int totalWeeklyHours;
        private final BigDecimal usagePercentage;
        private final boolean canBookMore;

        public BookingUsageStats(UUID userId, String username, BigDecimal ownershipPercentage,
                int hoursUsedThisWeek, int totalWeeklyHours) {
            this.userId = userId;
            this.username = username;
            this.ownershipPercentage = ownershipPercentage;
            this.hoursUsedThisWeek = hoursUsedThisWeek;
            this.totalWeeklyHours = totalWeeklyHours;
            this.usagePercentage = totalWeeklyHours > 0
                    ? BigDecimal.valueOf(hoursUsedThisWeek)
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(totalWeeklyHours), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            this.canBookMore = usagePercentage.compareTo(ownershipPercentage) <= 0;
        }

        // Getters
        public UUID getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public BigDecimal getOwnershipPercentage() {
            return ownershipPercentage;
        }

        public int getHoursUsedThisWeek() {
            return hoursUsedThisWeek;
        }

        public int getTotalWeeklyHours() {
            return totalWeeklyHours;
        }

        public BigDecimal getUsagePercentage() {
            return usagePercentage;
        }

        public boolean isCanBookMore() {
            return canBookMore;
        }
    }

    public static class BookingValidationResult {

        private final boolean isValid;
        private final String errorMessage;

        public BookingValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public static BookingValidationResult success() {
            return new BookingValidationResult(true, null);
        }

        public static BookingValidationResult error(String message) {
            return new BookingValidationResult(false, message);
        }
    }

    /**
     * Tạo booking mới
     */
    public LocalBooking createBooking(UUID userId, Long vehicleId, LocalDate bookingDate,
            LocalTime startTime, LocalTime endTime, String purpose, String notes) {

        // Validate booking
        BookingValidationResult validation = validateBooking(userId, vehicleId, bookingDate, startTime, endTime);
        if (!validation.isValid()) {
            throw new IllegalArgumentException(validation.getErrorMessage());
        }

        // Get contract and vehicle info
        LocalContract contract = contractLocalService.getContractByVehicleId(vehicleId);
        if (contract == null) {
            throw new IllegalArgumentException("Không tìm thấy hợp đồng cho xe này");
        }

        String username;
        try {
            var userProfile = userLocalService.getUserProfile(userId);
            username = userProfile.getUsername();
        } catch (Exception e) {
            throw new IllegalArgumentException("Không tìm thấy thông tin người dùng");
        }

        Long id = idCounter.getAndIncrement();
        LocalBooking booking = new LocalBooking(
                id, userId, username, vehicleId, contract.getVehicle().getName(),
                contract.getId(), contract.getContractNumber(), bookingDate, startTime, endTime,
                purpose, "CONFIRMED", notes, contract.getGroupId(), contract.getGroupName()
        );

        bookings.put(id, booking);
        return booking;
    }

    /**
     * Validate booking trước khi tạo
     */
    public BookingValidationResult validateBooking(UUID userId, Long vehicleId, LocalDate bookingDate,
            LocalTime startTime, LocalTime endTime) {
        // Check time logic
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            return BookingValidationResult.error("Thời gian kết thúc phải sau thời gian bắt đầu");
        }

        // Check date not in past
        if (bookingDate.isBefore(LocalDate.now())) {
            return BookingValidationResult.error("Không thể đặt lịch cho ngày trong quá khứ");
        }

        // Check contract exists and user is member
        LocalContract contract = contractLocalService.getContractByVehicleId(vehicleId);
        if (contract == null) {
            return BookingValidationResult.error("Không tìm thấy hợp đồng cho xe này");
        }

        boolean isContractMember = contract.getOwnerships().stream()
                .anyMatch(ownership -> ownership.getUserId().equals(userId));
        if (!isContractMember) {
            return BookingValidationResult.error("Bạn không có quyền đặt lịch xe này");
        }

        // Check time conflicts
        boolean hasTimeConflict = bookings.values().stream()
                .filter(b -> b.getVehicleId().equals(vehicleId))
                .filter(b -> b.getBookingDate().equals(bookingDate))
                .filter(b -> !"CANCELLED".equals(b.getStatus()))
                .anyMatch(b -> isTimeOverlap(startTime, endTime, b.getStartTime(), b.getEndTime()));

        if (hasTimeConflict) {
            return BookingValidationResult.error("Đã có lịch đặt xe trong khoảng thời gian này");
        }

        // Check usage percentage
        BookingUsageStats stats = getUserUsageStats(userId, vehicleId);
        if (!stats.canBookMore) {
            return BookingValidationResult.error(
                    String.format("Bạn đã sử dụng %.2f%% thời gian trong tuần (vượt quá %.2f%% quyền sở hữu)",
                            stats.getUsagePercentage(), stats.getOwnershipPercentage())
            );
        }

        return BookingValidationResult.success();
    }

    /**
     * Check time overlap
     */
    private boolean isTimeOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    /**
     * Get user usage statistics
     */
    public BookingUsageStats getUserUsageStats(UUID userId, Long vehicleId) {
        LocalContract contract = contractLocalService.getContractByVehicleId(vehicleId);
        if (contract == null) {
            throw new IllegalArgumentException("Không tìm thấy hợp đồng cho xe này");
        }

        // Get user ownership percentage
        BigDecimal ownershipPercentage = contract.getOwnerships().stream()
                .filter(ownership -> ownership.getUserId().equals(userId))
                .map(ownership -> ownership.getOwnershipPercentage())
                .findFirst()
                .orElse(BigDecimal.ZERO);

        // Get current week dates
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Calculate hours used this week
        int hoursUsedThisWeek = bookings.values().stream()
                .filter(b -> b.getUserId().equals(userId))
                .filter(b -> b.getVehicleId().equals(vehicleId))
                .filter(b -> !"CANCELLED".equals(b.getStatus()))
                .filter(b -> !b.getBookingDate().isBefore(startOfWeek) && !b.getBookingDate().isAfter(endOfWeek))
                .mapToInt(b -> calculateBookingHours(b.getStartTime(), b.getEndTime()))
                .sum();

        // Total weekly hours available (assumed 8 hours per day * 7 days = 56 hours)
        int totalWeeklyHours = 56;

        String username = userLocalService.getUsernameById(userId);

        return new BookingUsageStats(userId, username, ownershipPercentage,
                hoursUsedThisWeek, totalWeeklyHours);
    }

    /**
     * Calculate booking duration in hours
     */
    private int calculateBookingHours(LocalTime startTime, LocalTime endTime) {
        return (int) java.time.Duration.between(startTime, endTime).toHours();
    }

    /**
     * Get user's bookings
     */
    public List<LocalBooking> getUserBookings(UUID userId) {
        return bookings.values().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .sorted(Comparator.comparing(LocalBooking::getBookingDate).reversed()
                        .thenComparing(LocalBooking::getStartTime))
                .collect(Collectors.toList());
    }

    /**
     * Get vehicle bookings for a specific date
     */
    public List<LocalBooking> getVehicleBookingsForDate(Long vehicleId, LocalDate date) {
        return bookings.values().stream()
                .filter(booking -> booking.getVehicleId().equals(vehicleId))
                .filter(booking -> booking.getBookingDate().equals(date))
                .filter(booking -> !"CANCELLED".equals(booking.getStatus()))
                .sorted(Comparator.comparing(LocalBooking::getStartTime))
                .collect(Collectors.toList());
    }

    /**
     * Get available vehicles - simplified to allow any user to book any active
     * vehicle
     */
    public List<LocalContract> getUserAvailableVehicles(UUID userId) {
        // Return all active contracts - simplified approach
        // Note: userId parameter kept for interface consistency but not used in simplified logic
        return contractLocalService.getAllContracts().stream()
                .filter(contract -> "ACTIVE".equals(contract.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Cancel booking
     */
    public boolean cancelBooking(Long bookingId, UUID userId) {
        LocalBooking booking = bookings.get(bookingId);
        if (booking == null) {
            return false;
        }

        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền hủy lịch đặt này");
        }

        if ("CANCELLED".equals(booking.getStatus()) || "COMPLETED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Không thể hủy lịch đặt đã hoàn thành hoặc đã hủy");
        }

        booking.setStatus("CANCELLED");
        booking.setUpdatedAt(java.time.LocalDateTime.now());
        return true;
    }

    /**
     * Get booking by ID
     */
    public LocalBooking getBookingById(Long id) {
        return bookings.get(id);
    }

    /**
     * Get all bookings for a contract
     */
    public List<LocalBooking> getContractBookings(Long contractId) {
        return bookings.values().stream()
                .filter(booking -> booking.getContractId().equals(contractId))
                .sorted(Comparator.comparing(LocalBooking::getBookingDate).reversed()
                        .thenComparing(LocalBooking::getStartTime))
                .collect(Collectors.toList());
    }

    /**
     * Initialize with sample data
     */
    public void initializeSampleBookings() {
        if (!bookings.isEmpty()) {
            return;
        }

        // Sample booking data
        UUID user1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID user2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

        // Add some sample bookings
        LocalBooking booking1 = new LocalBooking(
                1L, user1, "nguyenvana", 1L, "Toyota Camry 2023", 1L, "CT001",
                LocalDate.now().plusDays(1), LocalTime.of(8, 0), LocalTime.of(12, 0),
                "Đi công tác", "CONFIRMED", "Cần đổ xăng trước khi trả xe", 1L, "Nhóm Công ty ABC"
        );
        bookings.put(1L, booking1);

        LocalBooking booking2 = new LocalBooking(
                2L, user2, "tranthib", 1L, "Toyota Camry 2023", 1L, "CT001",
                LocalDate.now().plusDays(2), LocalTime.of(14, 0), LocalTime.of(18, 0),
                "Đi họp khách hàng", "CONFIRMED", "", 1L, "Nhóm Công ty ABC"
        );
        bookings.put(2L, booking2);

        idCounter.set(3L);
    }
}
