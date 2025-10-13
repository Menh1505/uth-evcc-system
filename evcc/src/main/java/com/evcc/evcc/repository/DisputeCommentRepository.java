package com.evcc.evcc.repository;

import com.evcc.evcc.entity.DisputeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DisputeCommentRepository extends JpaRepository<DisputeComment, UUID> {
}