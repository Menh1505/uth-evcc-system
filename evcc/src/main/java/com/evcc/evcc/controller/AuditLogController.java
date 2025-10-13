package com.evcc.evcc.controller;

import com.evcc.evcc.entity.AuditLog;
import com.evcc.evcc.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {
    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public List<AuditLog> getAllAuditLogs() {
        return auditLogService.getAllAuditLogs();
    }

    @PostMapping
    public AuditLog saveAuditLog(@RequestBody AuditLog auditLog) {
        return auditLogService.saveAuditLog(auditLog);
    }

    @GetMapping("/{id}")
    public AuditLog getAuditLogById(@PathVariable UUID id) {
        return auditLogService.getAuditLogById(id);
    }
}