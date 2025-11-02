package com.evcc.document.repository;
import com.evcc.document.model.DocumentUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentUploadRepository extends JpaRepository<DocumentUpload, Long> {
}
