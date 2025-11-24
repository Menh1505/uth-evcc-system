package com.evcc.voting.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.evcc.group.entity.Group;
import com.evcc.user.entity.User;
import com.evcc.voting.enums.VoteStatus;
import com.evcc.voting.enums.VoteType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity đại diện cho cuộc bỏ phiếu trong nhóm đồng sở hữu xe
 */
@Entity
@Table(name = "group_votes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tiêu đề cuộc vote
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * Mô tả chi tiết
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Loại vote
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = false)
    private VoteType voteType;

    /**
     * Trạng thái vote
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VoteStatus status = VoteStatus.DRAFT;

    /**
     * Nhóm thực hiện vote
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    /**
     * Người tạo vote
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /**
     * Thời gian bắt đầu vote
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * Thời gian kết thúc vote
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * Số phiếu tối thiểu để vote có hiệu lực
     */
    @Column(name = "minimum_votes")
    @Builder.Default
    private Integer minimumVotes = 1;

    /**
     * Tỷ lệ phần trăm cần để thông qua (ví dụ: 50, 66.67)
     */
    @Column(name = "required_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal requiredPercentage = new BigDecimal("50.00");

    /**
     * Có cho phép thay đổi phiếu bầu không
     */
    @Column(name = "allow_vote_change")
    @Builder.Default
    private Boolean allowVoteChange = true;

    /**
     * Vote ẩn danh hay không
     */
    @Column(name = "anonymous")
    @Builder.Default
    private Boolean anonymous = false;

    /**
     * Số tiền liên quan (nếu vote về tài chính)
     */
    @Column(name = "related_amount", precision = 19, scale = 2)
    private BigDecimal relatedAmount;

    /**
     * ID liên quan (expense_id, vehicle_id, contract_id...)
     */
    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    /**
     * Loại entity liên quan (EXPENSE, VEHICLE, CONTRACT...)
     */
    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType;

    /**
     * Danh sách các lựa chọn vote
     */
    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VoteOption> options = new ArrayList<>();

    /**
     * Ghi chú thêm
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Kiểm tra xem vote có đang hoạt động không
     */
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == VoteStatus.ACTIVE
                && now.isAfter(startTime)
                && now.isBefore(endTime);
    }

    /**
     * Kiểm tra xem đã hết hạn chưa
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endTime);
    }

    /**
     * Tính tổng số phiếu đã bầu
     */
    public long getTotalVotes() {
        return options.stream()
                .mapToLong(VoteOption::getVoteCount)
                .sum();
    }

    /**
     * Kiểm tra xem có đủ số phiếu tối thiểu không
     */
    public boolean hasMinimumVotes() {
        return getTotalVotes() >= minimumVotes;
    }
}
