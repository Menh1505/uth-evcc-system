package com.evcc.audit.repository;

import com.evcc.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AuditRepository extends JpaRepository<AuditLog, UUID> {
    // (Chúng ta sẽ thêm các hàm tìm kiếm phức tạp hơn sau)
}