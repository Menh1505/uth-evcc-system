package com.evcc.document.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_uploads")
public class DocumentUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ownerId;
    private String documentType;
    private String fileName;
    private String filePath;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status = DocumentStatus.PENDING;

    private LocalDateTime uploadedAt = LocalDateTime.now();

    public DocumentUpload() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
