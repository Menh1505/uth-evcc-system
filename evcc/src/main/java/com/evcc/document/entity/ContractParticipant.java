package com.evcc.document.entity;

import com.evcc.document.enums.ParticipantStatus;
import com.evcc.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "contract_participants",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_contract_user", columnNames = {"contract_id", "user_id"})
    },
    indexes = {
        @Index(name = "idx_contract_participants_contract", columnList = "contract_id"),
        @Index(name = "idx_contract_participants_user", columnList = "user_id")
    }
)
public class ContractParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hợp đồng liên quan (bắt buộc)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    // Tên hiển thị tại thời điểm mời (bắt buộc)
    @Column(name = "invitee_name", nullable = false)
    private String inviteeName;

    // Người dùng được mời (bắt buộc) - mời bằng userId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;

    private LocalDateTime acceptedAt;
}
