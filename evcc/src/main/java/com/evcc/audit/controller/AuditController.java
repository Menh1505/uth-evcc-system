package com.evcc.audit.controller;

import com.evcc.audit.entity.AuditLog;
import com.evcc.audit.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    /**
     * API lấy tất cả nhật ký hệ thống
     * (Sau này PHẢI bảo vệ API này, chỉ cho ADMIN)
     */
    @GetMapping("/")
    // @PreAuthorize("hasRole('ADMIN')") // Sẽ thêm dòng này sau khi có module 'auth'
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        return ResponseEntity.ok(auditService.getAllLogs());
    }
}