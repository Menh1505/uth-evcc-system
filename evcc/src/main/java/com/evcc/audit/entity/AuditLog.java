package com.evcc.audit.entity;

import com.evcc.audit.enums.AuditAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID; // Dùng UUID giống User

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Chúng ta chỉ lưu ID của user, không cần liên kết @ManyToOne
    @Column(name = "user_id")
    private UUID userId; 

    // Tên của user (để xem log cho nhanh, không cần join)
    @Column(name = "username")
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action; // Hành động là gì?

    @Column(nullable = false)
    private String entityType; // Tác động lên đối tượng nào? (VD: "Vehicle", "Contract")

    private String entityId; // ID của đối tượng đó

    @Lob // Dùng @Lob cho các trường văn bản dài
    @Column(columnDefinition = "TEXT")
    private String details; // Chi tiết (VD: "Từ {A} sang {B}")

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}