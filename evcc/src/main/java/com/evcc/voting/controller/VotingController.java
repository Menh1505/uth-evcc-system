package com.evcc.voting.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evcc.security.service.AuthenticationService;
import com.evcc.voting.dto.request.CastVoteRequest;
import com.evcc.voting.dto.request.CreateVoteRequest;
import com.evcc.voting.dto.response.VoteResponse;
import com.evcc.voting.service.VotingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller cho voting system
 */
@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VotingController {

    private static final Logger logger = LoggerFactory.getLogger(VotingController.class);

    private final VotingService votingService;
    private final AuthenticationService authenticationService;

    /**
     * Tạo vote mới
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<VoteResponse> createVote(
            @Valid @RequestBody CreateVoteRequest request) {
        try {
            UUID userId = authenticationService.getCurrentUserId();
            VoteResponse response = votingService.createVote(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for creating vote: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating vote", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lấy danh sách vote của nhóm
     */
    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<VoteResponse>> getGroupVotes(@PathVariable Long groupId) {
        try {
            UUID userId = authenticationService.getCurrentUserId();
            List<VoteResponse> votes = votingService.getGroupVotes(groupId, userId);
            return ResponseEntity.ok(votes);
        } catch (Exception e) {
            logger.error("Error getting group votes for group: {}", groupId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lấy vote đang chờ user
     */
    @GetMapping("/group/{groupId}/pending")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<VoteResponse>> getPendingVotes(@PathVariable Long groupId) {
        try {
            UUID userId = authenticationService.getCurrentUserId();
            List<VoteResponse> votes = votingService.getPendingVotes(groupId, userId);
            return ResponseEntity.ok(votes);
        } catch (Exception e) {
            logger.error("Error getting pending votes for group: {}", groupId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lấy chi tiết vote
     */
    @GetMapping("/{voteId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<VoteResponse> getVoteDetail(@PathVariable Long voteId) {
        try {
            UUID userId = authenticationService.getCurrentUserId();
            VoteResponse vote = votingService.getVoteDetail(voteId, userId);
            return ResponseEntity.ok(vote);
        } catch (IllegalArgumentException e) {
            logger.warn("Vote not found: {}", voteId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting vote detail: {}", voteId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Bắt đầu vote (chuyển từ DRAFT sang ACTIVE)
     */
    @PutMapping("/{voteId}/start")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<VoteResponse> startVote(@PathVariable Long voteId) {
        try {
            UUID userId = authenticationService.getCurrentUserId();
            VoteResponse vote = votingService.startVote(voteId, userId);
            return ResponseEntity.ok(vote);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for starting vote {}: {}", voteId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error starting vote: {}", voteId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cast vote
     */
    @PostMapping("/cast")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<VoteResponse> castVote(
            @Valid @RequestBody CastVoteRequest request,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = authenticationService.getCurrentUserId();
            String ipAddress = getClientIpAddress(httpRequest);
            VoteResponse vote = votingService.castVote(request, userId, ipAddress);
            return ResponseEntity.ok(vote);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid vote cast request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error casting vote", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Đóng vote và tính kết quả
     */
    @PutMapping("/{voteId}/close")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<VoteResponse> closeVote(@PathVariable Long voteId) {
        try {
            UUID userId = authenticationService.getCurrentUserId();
            VoteResponse vote = votingService.closeVote(voteId, userId);
            return ResponseEntity.ok(vote);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for closing vote {}: {}", voteId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error closing vote: {}", voteId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lấy IP address của client
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null || xForwardedForHeader.isEmpty()) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0].trim();
        }
    }
}
