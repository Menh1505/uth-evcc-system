package com.evcc.voting.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evcc.group.entity.Group;
import com.evcc.group.repository.GroupRepository;
import com.evcc.user.entity.User;
import com.evcc.user.repository.UserRepository;
import com.evcc.voting.dto.request.CastVoteRequest;
import com.evcc.voting.dto.request.CreateVoteRequest;
import com.evcc.voting.dto.response.VoteOptionResponse;
import com.evcc.voting.dto.response.VoteRecordResponse;
import com.evcc.voting.dto.response.VoteResponse;
import com.evcc.voting.entity.Vote;
import com.evcc.voting.entity.VoteOption;
import com.evcc.voting.entity.VoteRecord;
import com.evcc.voting.enums.VoteStatus;
import com.evcc.voting.repository.VoteOptionRepository;
import com.evcc.voting.repository.VoteRecordRepository;
import com.evcc.voting.repository.VoteRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service xử lý logic nghiệp vụ cho voting system
 */
@Service
@Transactional
@RequiredArgsConstructor
public class VotingService {

    private static final Logger logger = LoggerFactory.getLogger(VotingService.class);

    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    /**
     * Tạo vote mới
     */
    public VoteResponse createVote(CreateVoteRequest request, UUID createdById) {
        logger.info("Creating new vote: {} by user: {}", request.getTitle(), createdById);

        // Validate
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("Thời gian kết thúc phải sau thời gian bắt đầu");
        }

        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhóm"));

        User creator = userRepository.findById(createdById)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user"));

        // TODO: Check if user is member of group
        // Create vote
        Vote vote = Vote.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .voteType(request.getVoteType())
                .status(VoteStatus.DRAFT)
                .group(group)
                .createdBy(creator)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .minimumVotes(request.getMinimumVotes())
                .requiredPercentage(request.getRequiredPercentage())
                .allowVoteChange(request.getAllowVoteChange())
                .anonymous(request.getAnonymous())
                .relatedAmount(request.getRelatedAmount())
                .relatedEntityId(request.getRelatedEntityId())
                .relatedEntityType(request.getRelatedEntityType())
                .notes(request.getNotes())
                .build();

        vote = voteRepository.save(vote);

        // Create options
        for (int i = 0; i < request.getOptions().size(); i++) {
            CreateVoteRequest.VoteOptionRequest optionReq = request.getOptions().get(i);
            VoteOption option = VoteOption.builder()
                    .vote(vote)
                    .optionText(optionReq.getOptionText())
                    .description(optionReq.getDescription())
                    .displayOrder(optionReq.getDisplayOrder() != null ? optionReq.getDisplayOrder() : i)
                    .build();
            voteOptionRepository.save(option);
        }

        logger.info("Created vote with ID: {}", vote.getId());
        return convertToVoteResponse(vote, createdById);
    }

    /**
     * Bắt đầu vote (chuyển từ DRAFT sang ACTIVE)
     */
    public VoteResponse startVote(Long voteId, UUID userId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vote"));

        // Check permissions
        if (!vote.getCreatedBy().getId().equals(userId)) {
            throw new IllegalArgumentException("Chỉ người tạo vote mới có thể bắt đầu vote");
        }

        if (vote.getStatus() != VoteStatus.DRAFT) {
            throw new IllegalArgumentException("Vote đã được bắt đầu hoặc kết thúc");
        }

        vote.setStatus(VoteStatus.ACTIVE);
        vote = voteRepository.save(vote);

        logger.info("Started vote ID: {} by user: {}", voteId, userId);
        return convertToVoteResponse(vote, userId);
    }

    /**
     * Cast vote
     */
    public VoteResponse castVote(CastVoteRequest request, UUID userId, String ipAddress) {
        Vote vote = voteRepository.findById(request.getVoteId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vote"));

        // Validate vote is active
        if (!vote.isActive()) {
            throw new IllegalArgumentException("Vote không đang hoạt động");
        }

        // Check if user already voted
        if (voteRecordRepository.existsByVote_IdAndUser_Id(request.getVoteId(), userId)) {
            if (!vote.getAllowVoteChange()) {
                throw new IllegalArgumentException("Bạn đã vote và không thể thay đổi");
            }
            // Remove previous vote
            voteRecordRepository.deleteByVote_IdAndUser_Id(request.getVoteId(), userId);
        }

        // Validate option belongs to vote
        VoteOption option = voteOptionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lựa chọn"));

        if (!option.getVote().getId().equals(request.getVoteId())) {
            throw new IllegalArgumentException("Lựa chọn không thuộc về vote này");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user"));

        // Create vote record
        VoteRecord voteRecord = VoteRecord.builder()
                .vote(vote)
                .voteOption(option)
                .user(user)
                .ipAddress(ipAddress)
                .comment(request.getComment())
                .build();

        voteRecordRepository.save(voteRecord);

        logger.info("User {} cast vote for option {} in vote {}", userId, request.getOptionId(), request.getVoteId());
        return convertToVoteResponse(vote, userId);
    }

    /**
     * Lấy danh sách vote của nhóm
     */
    @Transactional(readOnly = true)
    public List<VoteResponse> getGroupVotes(Long groupId, UUID userId) {
        List<Vote> votes = voteRepository.findByGroup_IdOrderByCreatedAtDesc(groupId);
        return votes.stream()
                .map(vote -> convertToVoteResponse(vote, userId))
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết vote
     */
    @Transactional(readOnly = true)
    public VoteResponse getVoteDetail(Long voteId, UUID userId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vote"));

        return convertToVoteResponse(vote, userId);
    }

    /**
     * Lấy vote đang chờ user vote
     */
    @Transactional(readOnly = true)
    public List<VoteResponse> getPendingVotes(Long groupId, UUID userId) {
        List<Vote> votes = voteRepository.findPendingVotesForUser(groupId, userId, LocalDateTime.now());
        return votes.stream()
                .map(vote -> convertToVoteResponse(vote, userId))
                .collect(Collectors.toList());
    }

    /**
     * Đóng vote và tính kết quả
     */
    public VoteResponse closeVote(Long voteId, UUID userId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vote"));

        // Check permissions
        if (!vote.getCreatedBy().getId().equals(userId)) {
            throw new IllegalArgumentException("Chỉ người tạo vote mới có thể đóng vote");
        }

        if (vote.getStatus() != VoteStatus.ACTIVE) {
            throw new IllegalArgumentException("Vote không đang hoạt động");
        }

        // Calculate result
        vote.setStatus(VoteStatus.CLOSED);

        // Check if vote passed
        if (vote.hasMinimumVotes()) {
            List<VoteOption> winningOptions = voteOptionRepository.findWinningOptions(voteId);
            if (!winningOptions.isEmpty()) {
                VoteOption winner = winningOptions.get(0);
                double winnerPercentage = winner.getVotePercentage();

                if (winnerPercentage >= vote.getRequiredPercentage().doubleValue()) {
                    vote.setStatus(VoteStatus.APPROVED);
                } else {
                    vote.setStatus(VoteStatus.REJECTED);
                }
            }
        } else {
            vote.setStatus(VoteStatus.REJECTED);
        }

        vote = voteRepository.save(vote);

        logger.info("Closed vote ID: {} with status: {}", voteId, vote.getStatus());
        return convertToVoteResponse(vote, userId);
    }

    /**
     * Xử lý vote hết hạn tự động
     */
    @Transactional
    public void processExpiredVotes() {
        List<Vote> expiredVotes = voteRepository.findExpiredActiveVotes(LocalDateTime.now());

        for (Vote vote : expiredVotes) {
            logger.info("Processing expired vote ID: {}", vote.getId());
            closeVote(vote.getId(), vote.getCreatedBy().getId());
        }
    }

    /**
     * Convert Vote entity to VoteResponse DTO
     */
    private VoteResponse convertToVoteResponse(Vote vote, UUID userId) {
        // Check if user has voted
        boolean userHasVoted = voteRecordRepository.existsByVote_IdAndUser_Id(vote.getId(), userId);
        Long userVotedOptionId = null;

        if (userHasVoted) {
            voteRecordRepository.findByVote_IdAndUser_Id(vote.getId(), userId)
                    .ifPresent(record -> {
                        // userVotedOptionId = record.getVoteOption().getId(); // This would need to be set properly
                    });
        }

        // Convert options
        List<VoteOption> options = voteOptionRepository.findByVote_IdOrderByDisplayOrderAsc(vote.getId());
        List<VoteOptionResponse> optionResponses = options.stream()
                .map(option -> convertToVoteOptionResponse(option, vote.getAnonymous()))
                .collect(Collectors.toList());

        return VoteResponse.builder()
                .id(vote.getId())
                .title(vote.getTitle())
                .description(vote.getDescription())
                .voteType(vote.getVoteType())
                .status(vote.getStatus())
                .groupId(vote.getGroup().getId())
                .groupName(vote.getGroup().getName())
                .createdBy(vote.getCreatedBy().getId().toString())
                .createdByUsername(vote.getCreatedBy().getUsername())
                .startTime(vote.getStartTime())
                .endTime(vote.getEndTime())
                .minimumVotes(vote.getMinimumVotes())
                .requiredPercentage(vote.getRequiredPercentage())
                .allowVoteChange(vote.getAllowVoteChange())
                .anonymous(vote.getAnonymous())
                .relatedAmount(vote.getRelatedAmount())
                .relatedEntityId(vote.getRelatedEntityId())
                .relatedEntityType(vote.getRelatedEntityType())
                .notes(vote.getNotes())
                .options(optionResponses)
                .totalVotes(vote.getTotalVotes())
                .isActive(vote.isActive())
                .isExpired(vote.isExpired())
                .hasMinimumVotes(vote.hasMinimumVotes())
                .userHasVoted(userHasVoted)
                .userVotedOptionId(userVotedOptionId)
                .createdAt(vote.getCreatedAt())
                .updatedAt(vote.getUpdatedAt())
                .build();
    }

    /**
     * Convert VoteOption to VoteOptionResponse
     */
    private VoteOptionResponse convertToVoteOptionResponse(VoteOption option, Boolean isAnonymous) {
        List<VoteRecordResponse> recordResponses = null;

        if (!isAnonymous) {
            recordResponses = option.getVoteRecords().stream()
                    .map(record -> VoteRecordResponse.builder()
                    .id(record.getId())
                    .userId(record.getUser().getId())
                    .username(record.getUser().getUsername())
                    .votedAt(record.getVotedAt())
                    .comment(record.getComment())
                    .build())
                    .collect(Collectors.toList());
        }

        return VoteOptionResponse.builder()
                .id(option.getId())
                .optionText(option.getOptionText())
                .description(option.getDescription())
                .displayOrder(option.getDisplayOrder())
                .voteCount(option.getVoteCount())
                .votePercentage(option.getVotePercentage())
                .voteRecords(recordResponses)
                .createdAt(option.getCreatedAt())
                .updatedAt(option.getUpdatedAt())
                .build();
    }
}
