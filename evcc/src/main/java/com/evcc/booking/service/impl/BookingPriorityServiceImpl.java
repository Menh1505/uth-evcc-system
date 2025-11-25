package com.evcc.booking.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.evcc.booking.dto.BookingPriorityInfo;
import com.evcc.booking.dto.PriorityCalculationResult;
import com.evcc.booking.entity.VehicleBooking;
import com.evcc.booking.enums.BookingStatus;
import com.evcc.booking.repository.VehicleBookingRepository;
import com.evcc.booking.service.BookingPriorityService;
import com.evcc.contract.entity.Contract;
import com.evcc.contract.repository.ContractRepository;
import com.evcc.user.entity.User;
import com.evcc.user.repository.UserRepository;

/**
 * Implementation của BookingPriorityService Tính toán ưu tiên đặt lịch dựa trên
 * tỉ lệ sở hữu và lịch sử sử dụng
 */
@Service
public class BookingPriorityServiceImpl implements BookingPriorityService {

    private static final Logger logger = LoggerFactory.getLogger(BookingPriorityServiceImpl.class);

    // Thông số tính toán ưu tiên
    private static final int MAX_PRIORITY_SCORE = 100;
    private static final int OWNERSHIP_WEIGHT = 40; // 40% weight cho ownership
    private static final int USAGE_HISTORY_WEIGHT = 30; // 30% weight cho usage history  
    private static final int RECENT_ACTIVITY_WEIGHT = 30; // 30% weight cho recent activity

    // Thông số thời gian
    private static final int RECENT_DAYS_THRESHOLD = 30;
    private static final int BOOKING_ADVANCE_LIMIT_DAYS = 30;

    private final VehicleBookingRepository bookingRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;

    public BookingPriorityServiceImpl(VehicleBookingRepository bookingRepository,
            ContractRepository contractRepository,
            UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.contractRepository = contractRepository;
        this.userRepository = userRepository;
    }

    @Override
    public int calculateUserPriority(Long contractId, UUID userId) {
        return calculateUserPriorityAtTime(contractId, userId, LocalDateTime.now());
    }

    @Override
    public int calculateUserPriorityAtTime(Long contractId, UUID userId, LocalDateTime atTime) {
        try {
            Contract contract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

            // Tính điểm ownership (dựa trên tỉ lệ sở hữu trong contract)
            int ownershipScore = calculateOwnershipScore(contract, userId);

            // Tính điểm usage history (dựa trên lịch sử sử dụng)
            int usageHistoryScore = calculateUsageHistoryScore(contractId, userId, atTime);

            // Tính điểm recent activity (dựa trên hoạt động gần đây)
            int recentActivityScore = calculateRecentActivityScore(contractId, userId, atTime);

            // Tổng hợp điểm với trọng số
            int totalScore = (ownershipScore * OWNERSHIP_WEIGHT
                    + usageHistoryScore * USAGE_HISTORY_WEIGHT
                    + recentActivityScore * RECENT_ACTIVITY_WEIGHT) / 100;

            return Math.min(totalScore, MAX_PRIORITY_SCORE);

        } catch (Exception e) {
            logger.error("Error calculating user priority for user {} in contract {}: {}",
                    userId, contractId, e.getMessage());
            return 50; // Default neutral priority
        }
    }

    @Override
    public List<BookingPriorityInfo> getContractMembersPriorityList(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));

        List<BookingPriorityInfo> priorityList = new ArrayList<>();

        // Get all users in contract and calculate their priority
        // Assuming contract has a method to get users - this may need adjustment based on your Contract entity
        // For now, getting users from recent bookings in this contract
        List<UUID> contractUsers = bookingRepository.findByContractId(contractId)
                .stream()
                .map(booking -> booking.getUser().getId())
                .distinct()
                .collect(Collectors.toList());

        for (UUID userId : contractUsers) {
            int priority = calculateUserPriority(contractId, userId);
            BookingPriorityInfo.UsageStats stats = getUserUsageStats(userId, contractId, RECENT_DAYS_THRESHOLD);

            BookingPriorityInfo info = BookingPriorityInfo.builder()
                    .userId(userId)
                    .priorityScore(priority)
                    .recentUsage(stats)
                    .build();

            priorityList.add(info);
        }

        return priorityList.stream()
                .sorted(Comparator.comparingInt(BookingPriorityInfo::getPriorityScore).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public PriorityCalculationResult calculateDetailedPriority(Long contractId, UUID userId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));

        LocalDateTime now = LocalDateTime.now();

        int ownershipScore = calculateOwnershipScore(contract, userId);
        int usageHistoryScore = calculateUsageHistoryScore(contractId, userId, now);
        int recentActivityScore = calculateRecentActivityScore(contractId, userId, now);
        BigDecimal recentUsageAdjustment = calculateRecentUsageAdjustment(userId, contractId, RECENT_DAYS_THRESHOLD);

        int totalScore = (ownershipScore * OWNERSHIP_WEIGHT
                + usageHistoryScore * USAGE_HISTORY_WEIGHT
                + recentActivityScore * RECENT_ACTIVITY_WEIGHT) / 100;

        return PriorityCalculationResult.builder()
                .userId(userId)
                .contractId(contractId)
                .ownershipBaseScore(ownershipScore)
                .usageHistoryScore(usageHistoryScore)
                .recentActivityScore(recentActivityScore)
                .usageAdjustmentFactor(recentUsageAdjustment)
                .totalScore(Math.min(totalScore, MAX_PRIORITY_SCORE))
                .calculatedAt(now)
                .build();
    }

    @Override
    public boolean canUserBookAtTime(Long contractId, UUID userId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // Kiểm tra thời gian booking không quá xa
            if (startTime.isAfter(LocalDateTime.now().plusDays(BOOKING_ADVANCE_LIMIT_DAYS))) {
                return false;
            }

            // Kiểm tra user có trong contract
            int userPriority = calculateUserPriorityAtTime(contractId, userId, startTime);

            // Kiểm tra conflict với bookings khác có priority cao hơn
            List<VehicleBooking> conflictingBookings = bookingRepository
                    .findConflictingBookings(contractId, startTime, endTime);

            for (VehicleBooking booking : conflictingBookings) {
                if (booking.getStatus() == BookingStatus.CONFIRMED
                        || booking.getStatus() == BookingStatus.IN_PROGRESS) {

                    int otherUserPriority = calculateUserPriorityAtTime(contractId, booking.getUser().getId(), startTime);
                    if (otherUserPriority > userPriority) {
                        return false; // Other user has higher priority
                    }
                }
            }

            return true;

        } catch (Exception e) {
            logger.error("Error checking if user can book at time: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public LocalDateTime findBestAvailableTime(Long contractId, UUID userId, LocalDateTime preferredStart,
            int duration, int flexibilityHours) {
        LocalDateTime searchStart = preferredStart.minusHours(flexibilityHours);
        LocalDateTime searchEnd = preferredStart.plusHours(flexibilityHours);

        // Try every 30-minute slot within the flexibility window
        LocalDateTime current = searchStart;
        while (current.isBefore(searchEnd)) {
            LocalDateTime slotEnd = current.plusMinutes(duration);

            if (canUserBookAtTime(contractId, userId, current, slotEnd)) {
                return current;
            }

            current = current.plusMinutes(30);
        }

        return null; // No available time found
    }

    @Override
    public void updatePriorityAfterBookingCompletion(Long bookingId) {
        // This could be used to update priority adjustments after booking completion
        // For now, just log the completion
        logger.info("Booking {} completed, priority adjustments will be recalculated on next request", bookingId);
    }

    @Override
    public BigDecimal calculateRecentUsageAdjustment(UUID userId, Long contractId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        List<VehicleBooking> recentBookings = bookingRepository
                .findByUserIdAndContractIdAndStartTimeAfter(userId, contractId, since);

        // Calculate total hours used
        long totalMinutesUsed = recentBookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.COMPLETED)
                .mapToLong(booking -> ChronoUnit.MINUTES.between(booking.getStartTime(), booking.getEndTime()))
                .sum();

        // Calculate average usage per day
        double avgHoursPerDay = (double) totalMinutesUsed / (days * 60);

        // Adjustment factor: less recent usage = higher priority
        // Formula: 1.5 - (avgHoursPerDay / 12) * 1.0
        // This gives range from 0.5 (high usage) to 1.5 (low usage)
        BigDecimal adjustment = BigDecimal.valueOf(1.5 - Math.min(avgHoursPerDay / 12.0, 1.0));
        return adjustment.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BookingPriorityInfo.UsageStats getUserUsageStats(UUID userId, Long contractId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        List<VehicleBooking> bookings = bookingRepository
                .findByUserIdAndContractIdAndStartTimeAfter(userId, contractId, since);

        long totalBookings = bookings.size();
        long completedBookings = bookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.COMPLETED)
                .count();

        long totalMinutesUsed = bookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.COMPLETED)
                .mapToLong(booking -> ChronoUnit.MINUTES.between(booking.getStartTime(), booking.getEndTime()))
                .sum();

        return BookingPriorityInfo.UsageStats.builder()
                .totalTripsLast30Days((int) totalBookings)
                .totalHoursLast30Days((int) completedBookings)
                .averageRating(BigDecimal.valueOf(totalMinutesUsed / 60.0).setScale(1, RoundingMode.HALF_UP))
                .completionRate(BigDecimal.valueOf(totalMinutesUsed / (days * 60.0)).setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    // Private helper methods
    private int calculateOwnershipScore(Contract contract, UUID userId) {
        // Simplified calculation - in real implementation this would be based on actual ownership percentage
        // For now, assume equal ownership among all members
        // This should be adjusted based on your Contract entity structure
        return 80; // Default ownership score
    }

    private int calculateUsageHistoryScore(Long contractId, UUID userId, LocalDateTime atTime) {
        // Get usage in last 90 days before the given time
        LocalDateTime since = atTime.minusDays(90);

        List<VehicleBooking> historicalBookings = bookingRepository
                .findByUserIdAndContractIdAndStartTimeBetween(userId, contractId, since, atTime);

        // Less historical usage = higher priority
        long totalHours = historicalBookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.COMPLETED)
                .mapToLong(booking -> ChronoUnit.HOURS.between(booking.getStartTime(), booking.getEndTime()))
                .sum();

        // Convert to score: less usage = higher score
        // Formula: max 100 points, decrease by 2 points per hour of usage
        return Math.max(20, 100 - (int) Math.min(totalHours * 2, 80));
    }

    private int calculateRecentActivityScore(Long contractId, UUID userId, LocalDateTime atTime) {
        // Get usage in last 7 days before the given time
        LocalDateTime since = atTime.minusDays(7);

        List<VehicleBooking> recentBookings = bookingRepository
                .findByUserIdAndContractIdAndStartTimeBetween(userId, contractId, since, atTime);

        // Recent low usage = higher priority
        int recentBookingCount = recentBookings.size();

        // Formula: max 100 points, decrease by 20 points per recent booking
        return Math.max(20, 100 - (recentBookingCount * 20));
    }
}
