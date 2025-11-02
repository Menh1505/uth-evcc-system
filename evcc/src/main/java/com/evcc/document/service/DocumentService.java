package com.evcc.document.service;

import com.evcc.document.model.DocumentUpload;
import com.evcc.document.model.DocumentStatus;
import com.evcc.document.repository.DocumentUploadRepository;
import com.evcc.document.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentUploadRepository documentRepo;

    @Autowired
    private StorageService storageService;

    public DocumentUpload uploadDocument(String ownerId, String type, MultipartFile file) {
        String path = storageService.storeFile(file);

        DocumentUpload doc = new DocumentUpload();
        doc.setOwnerId(ownerId);
        doc.setDocumentType(type);
        doc.setFileName(file.getOriginalFilename());
        doc.setFilePath(path);
        doc.setStatus(DocumentStatus.PENDING);

        return documentRepo.save(doc);
    }

    public List<DocumentUpload> getAllDocuments() {
        return documentRepo.findAll();
    }

    public DocumentUpload approveDocument(Long id) {
        DocumentUpload doc = documentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay tai lieu"));
        doc.setStatus(DocumentStatus.APPROVED);
        return documentRepo.save(doc);
    }

    public DocumentUpload rejectDocument(Long id) {
        DocumentUpload doc = documentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay tai lieu"));
        doc.setStatus(DocumentStatus.REJECTED);
        return documentRepo.save(doc);
    }
}

