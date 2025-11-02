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
@Table(name = "contract_participants")
public class ContractParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hợp đồng liên quan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    // Thông tin người được mời (theo yêu cầu của bạn)
    @Column(nullable = false)
    private String inviteeName; // Tên

    @Column(nullable = false, unique = true)
    private String inviteePhone; // Số điện thoại

    // Tài khoản User liên kết (sẽ được gán khi họ đăng nhập và xác thực)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;

    private LocalDateTime acceptedAt;
}