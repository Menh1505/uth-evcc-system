package com.evcc.evcc.service;

import com.evcc.evcc.entity.AuditLog;
import com.evcc.evcc.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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