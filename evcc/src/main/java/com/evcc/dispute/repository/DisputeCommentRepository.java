package com.evcc.dispute.repository;

import com.evcc.dispute.entity.DisputeComment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface DisputeCommentRepository extends JpaRepository<DisputeComment, UUID> {
}
