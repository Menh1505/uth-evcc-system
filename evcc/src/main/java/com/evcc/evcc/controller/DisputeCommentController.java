package com.evcc.evcc.controller;

import com.evcc.evcc.entity.DisputeComment;
import com.evcc.evcc.service.DisputeCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/dispute-comments")
public class DisputeCommentController {
    @Autowired
    private DisputeCommentService disputeCommentService;

    @GetMapping
    public List<DisputeComment> getAllDisputeComments() {
        return disputeCommentService.getAllDisputeComments();
    }

    @PostMapping
    public DisputeComment saveDisputeComment(@RequestBody DisputeComment disputeComment) {
        return disputeCommentService.saveDisputeComment(disputeComment);
    }

    @GetMapping("/{id}")
    public DisputeComment getDisputeCommentById(@PathVariable UUID id) {
        return disputeCommentService.getDisputeCommentById(id);
    }
}