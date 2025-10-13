package com.evcc.evcc.service;

import com.evcc.evcc.entity.DisputeComment;
import com.evcc.evcc.repository.DisputeCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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