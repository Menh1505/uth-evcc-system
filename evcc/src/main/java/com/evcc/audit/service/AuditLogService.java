package com.evcc.audit.service;

import com.evcc.audit.entity.AuditLog;
import com.evcc.audit.repository.AuditLogRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service
public class AuditLogService {
    @Autowired
    private AuditLogRepository auditLogRepository;

    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }

    public AuditLog saveAuditLog(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    public AuditLog getAuditLogById(UUID id) {
        return auditLogRepository.findById(id).orElse(null);
    }
}
