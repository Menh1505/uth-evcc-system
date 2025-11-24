package com.evcc.voting.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Entity đại diện cho một lựa chọn trong cuộc vote
 */
@Entity
@Table(name = "vote_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Cuộc vote chứa option này
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    /**
     * Tên lựa chọn (VD: "Đồng ý", "Không đồng ý", "Phương án A")
     */
    @Column(nullable = false, length = 255)
    private String optionText;

    /**
     * Mô tả chi tiết lựa chọn
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Thứ tự hiển thị
     */
    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    /**
     * Danh sách các vote record cho option này
     */
    @OneToMany(mappedBy = "voteOption", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VoteRecord> voteRecords = new ArrayList<>();

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
     * Đếm số phiếu cho option này
     */
    public long getVoteCount() {
        return voteRecords != null ? voteRecords.size() : 0;
    }

    /**
     * Tính phần trăm phiếu (so với tổng phiếu của vote)
     */
    public double getVotePercentage() {
        if (vote == null) {
            return 0.0;
        }

        long totalVotes = vote.getTotalVotes();
        if (totalVotes == 0) {
            return 0.0;
        }

        return (getVoteCount() * 100.0) / totalVotes;
    }
}
