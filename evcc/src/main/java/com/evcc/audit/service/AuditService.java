package com.evcc.audit.service;

import com.evcc.audit.entity.AuditLog;
import com.evcc.audit.enums.AuditAction;
import com.evcc.audit.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AuditService {

    @Autowired
    private AuditRepository auditRepository;

    /**
     * Hàm chính để ghi lại nhật ký hành động.
     * Đây là hàm mà các service khác sẽ gọi.
     *
     * @param userId ID của user thực hiện (có thể null nếu là hệ thống)
     * @param username Tên user (để xem log nhanh)
     * @param action Hành động (CREATE, UPDATE, ...)
     * @param entityType Loại đối tượng (VD: "Vehicle", "Contract")
     * @param entityId ID của đối tượng
     * @param details Chi tiết thay đổi
     */
    public void logAction(UUID userId, String username, AuditAction action, String entityType, String entityId, String details) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .username(username)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();
        
        auditRepository.save(log);
    }

    /**
     * Lấy tất cả nhật ký (dùng cho API của Admin)
     */
    public List<AuditLog> getAllLogs() {
        // Tạm thời lấy tất cả, sau này sẽ thêm phân trang (Pagination)
        return auditRepository.findAll();
    }
}