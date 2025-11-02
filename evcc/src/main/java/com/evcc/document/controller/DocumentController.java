package com.evcc.document.controller;

import com.evcc.document.model.DocumentUpload;
import com.evcc.document.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<DocumentUpload> upload(
            @RequestParam("ownerId") String ownerId,
            @RequestParam("documentType") String documentType,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(documentService.uploadDocument(ownerId, documentType, file));
    }

    @GetMapping
    public ResponseEntity<List<DocumentUpload>> getAll() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<DocumentUpload> approve(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.approveDocument(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<DocumentUpload> reject(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.rejectDocument(id));
    }
}
