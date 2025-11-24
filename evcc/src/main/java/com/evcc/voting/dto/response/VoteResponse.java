package com.evcc.voting.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.evcc.voting.enums.VoteStatus;
import com.evcc.voting.enums.VoteType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO response cho vote
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponse {

    private Long id;
    private String title;
    private String description;
    private VoteType voteType;
    private String voteTypeDisplay;
    private VoteStatus status;
    private String statusDisplay;

    private Long groupId;
    private String groupName;

    private String createdBy;
    private String createdByUsername;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer minimumVotes;
    private BigDecimal requiredPercentage;
    private Boolean allowVoteChange;
    private Boolean anonymous;

    private BigDecimal relatedAmount;
    private Long relatedEntityId;
    private String relatedEntityType;
    private String notes;

    private List<VoteOptionResponse> options;

    // Computed fields
    private Long totalVotes;
    private Boolean isActive;
    private Boolean isExpired;
    private Boolean hasMinimumVotes;
    private Boolean userHasVoted;
    private Long userVotedOptionId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Lấy display text cho vote type
     */
    public String getVoteTypeDisplay() {
        if (voteType == null) {
            return "";
        }

        switch (voteType) {
            case EXPENSE_APPROVAL:
                return "Phê duyệt chi phí";
            case FUND_ALLOCATION:
                return "Phân bổ quỹ";
            case VEHICLE_PURCHASE:
                return "Mua xe mới";
            case VEHICLE_SALE:
                return "Bán xe";
            case GROUP_POLICY:
                return "Quy định nhóm";
            case MEMBER_MANAGEMENT:
                return "Quản lý thành viên";
            case BUDGET_PLANNING:
                return "Lập ngân sách";
            case MAINTENANCE_SCHEDULE:
                return "Lịch bảo trì";
            case OTHER:
                return "Khác";
            default:
                return voteType.toString();
        }
    }

    /**
     * Lấy display text cho status
     */
    public String getStatusDisplay() {
        if (status == null) {
            return "";
        }

        switch (status) {
            case DRAFT:
                return "Bản nháp";
            case ACTIVE:
                return "Đang diễn ra";
            case CLOSED:
                return "Đã kết thúc";
            case CANCELLED:
                return "Đã hủy";
            case APPROVED:
                return "Đã thông qua";
            case REJECTED:
                return "Đã từ chối";
            default:
                return status.toString();
        }
    }
}
