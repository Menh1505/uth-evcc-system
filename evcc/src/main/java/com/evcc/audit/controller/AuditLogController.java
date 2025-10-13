package com.evcc.audit.controller;

import com.evcc.audit.entity.AuditLog;
import com.evcc.audit.service.AuditLogService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




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
