package com.evcc.dispute.service;

import com.evcc.dispute.entity.DisputeComment;
import com.evcc.dispute.repository.DisputeCommentRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service
public class DisputeCommentService {
    @Autowired
    private DisputeCommentRepository disputeCommentRepository;

    public List<DisputeComment> getAllDisputeComments() {
        return disputeCommentRepository.findAll();
    }

    public DisputeComment saveDisputeComment(DisputeComment disputeComment) {
        return disputeCommentRepository.save(disputeComment);
    }

    public DisputeComment getDisputeCommentById(UUID id) {
        return disputeCommentRepository.findById(id).orElse(null);
    }
}
