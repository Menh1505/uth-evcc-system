package com.evcc.document.entity;

import com.evcc.document.enums.ContractStatus;
import com.evcc.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Tiêu đề hợp đồng

    @Lob // Dùng @Lob cho các trường văn bản dài (nội dung hợp đồng)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // Nội dung hợp đồng đã điền từ form

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    // Người tạo hợp đồng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    // Admin duyệt hợp đồng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_approver_id")
    private User adminApprover;

    // *** YÊU CẦU "SMART CONTRACT" ***
    // Lưu một bản "hash" (SHA-256) của nội dung.
    // Dùng để đảm bảo nội dung không bị thay đổi sau khi gửi.
    @Column(nullable = false, name = "content_hash")
    private String contentHash;

    // Danh sách những người tham gia
    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    private Set<ContractParticipant> participants;

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}