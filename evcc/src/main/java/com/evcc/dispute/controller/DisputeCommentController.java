package com.evcc.dispute.controller;

import com.evcc.dispute.entity.DisputeComment;
import com.evcc.dispute.service.DisputeCommentService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




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
