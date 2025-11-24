package com.evcc.voting.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evcc.expense.entity.VehicleExpense;
import com.evcc.expense.repository.VehicleExpenseRepository;
import com.evcc.voting.dto.request.CreateVoteRequest;
import com.evcc.voting.dto.response.VoteResponse;
import com.evcc.voting.entity.Vote;
import com.evcc.voting.enums.VoteStatus;
import com.evcc.voting.enums.VoteType;
import com.evcc.voting.repository.VoteRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service để tích hợp voting với quỹ tài chính nhóm
 */
@Service
@Transactional
@RequiredArgsConstructor
public class GroupFundVotingService {

    private static final Logger logger = LoggerFactory.getLogger(GroupFundVotingService.class);

    private final VotingService votingService;
    private final VoteRepository voteRepository;
    private final VehicleExpenseRepository vehicleExpenseRepository;

    /**
     * Tạo vote cho phê duyệt chi phí
     */
    public VoteResponse createExpenseApprovalVote(Long expenseId, UUID createdById) {
        VehicleExpense expense = vehicleExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khoản chi phí"));

        // Kiểm tra đã có vote cho expense này chưa
        if (voteRepository.existsActiveVoteForEntity("EXPENSE", expenseId, LocalDateTime.now())) {
            throw new IllegalArgumentException("Đã có vote đang hoạt động cho khoản chi phí này");
        }

        // Tạo vote request
        CreateVoteRequest request = CreateVoteRequest.builder()
                .title("Phê duyệt chi phí: " + expense.getDescription())
                .description(buildExpenseDescription(expense))
                .voteType(VoteType.EXPENSE_APPROVAL)
                .groupId(expense.getContract().getGroup().getId())
                .startTime(LocalDateTime.now().plusMinutes(5)) // Bắt đầu sau 5 phút
                .endTime(LocalDateTime.now().plusDays(3)) // Kết thúc sau 3 ngày
                .minimumVotes(1)
                .requiredPercentage(new BigDecimal("50.00"))
                .allowVoteChange(true)
                .anonymous(false)
                .relatedAmount(expense.getTotalAmount())
                .relatedEntityId(expenseId)
                .relatedEntityType("EXPENSE")
                .notes("Vote tự động tạo để phê duyệt chi phí")
                .options(createDefaultApprovalOptions())
                .build();

        return votingService.createVote(request, createdById);
    }

    /**
     * Tạo vote cho phân bổ quỹ nhóm
     */
    public VoteResponse createFundAllocationVote(Long groupId, BigDecimal amount, String purpose, UUID createdById) {
        CreateVoteRequest request = CreateVoteRequest.builder()
                .title("Phân bổ quỹ nhóm: " + purpose)
                .description("Phân bổ " + formatCurrency(amount) + " từ quỹ nhóm cho mục đích: " + purpose)
                .voteType(VoteType.FUND_ALLOCATION)
                .groupId(groupId)
                .startTime(LocalDateTime.now().plusMinutes(5))
                .endTime(LocalDateTime.now().plusDays(7)) // 7 ngày cho fund allocation
                .minimumVotes(2) // Ít nhất 2 người vote
                .requiredPercentage(new BigDecimal("66.67")) // Cần 2/3 đồng ý
                .allowVoteChange(true)
                .anonymous(false)
                .relatedAmount(amount)
                .relatedEntityType("FUND_ALLOCATION")
                .notes("Vote phân bổ quỹ tài chính nhóm")
                .options(createDefaultApprovalOptions())
                .build();

        return votingService.createVote(request, createdById);
    }

    /**
     * Xử lý kết quả vote khi vote kết thúc
     */
    public void processVoteResult(Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vote"));

        if (vote.getStatus() != VoteStatus.APPROVED && vote.getStatus() != VoteStatus.REJECTED) {
            return; // Chỉ xử lý vote đã có kết quả
        }

        logger.info("Processing vote result for vote ID: {} with status: {}", voteId, vote.getStatus());

        if (vote.getVoteType() == VoteType.EXPENSE_APPROVAL && vote.getRelatedEntityId() != null) {
            processExpenseApprovalResult(vote);
        } else if (vote.getVoteType() == VoteType.FUND_ALLOCATION) {
            processFundAllocationResult(vote);
        }
    }

    /**
     * Xử lý kết quả vote phê duyệt chi phí
     */
    private void processExpenseApprovalResult(Vote vote) {
        Long expenseId = vote.getRelatedEntityId();
        VehicleExpense expense = vehicleExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khoản chi phí"));

        if (vote.getStatus() == VoteStatus.APPROVED) {
            expense.setStatus("APPROVED");
            expense.setApprovedAt(LocalDateTime.now());
            expense.setApprovedBy("VOTING_SYSTEM");
            logger.info("Expense {} approved via voting", expenseId);
        } else {
            expense.setStatus("REJECTED");
            logger.info("Expense {} rejected via voting", expenseId);
        }

        vehicleExpenseRepository.save(expense);
    }

    /**
     * Xử lý kết quả vote phân bổ quỹ
     */
    private void processFundAllocationResult(Vote vote) {
        if (vote.getStatus() == VoteStatus.APPROVED) {
            // TODO: Implement fund allocation logic
            logger.info("Fund allocation approved for amount: {}", vote.getRelatedAmount());
        } else {
            logger.info("Fund allocation rejected for amount: {}", vote.getRelatedAmount());
        }
    }

    /**
     * Tạo mô tả cho vote phê duyệt chi phí
     */
    private String buildExpenseDescription(VehicleExpense expense) {
        StringBuilder desc = new StringBuilder();
        desc.append("Chi tiết khoản chi phí cần phê duyệt:\n\n");
        desc.append("• Mô tả: ").append(expense.getDescription()).append("\n");
        desc.append("• Số tiền: ").append(formatCurrency(expense.getTotalAmount())).append("\n");
        desc.append("• Loại: ").append(expense.getExpenseType()).append("\n");

        if (expense.getDueDate() != null) {
            desc.append("• Hạn thanh toán: ").append(expense.getDueDate()).append("\n");
        }

        if (expense.getNotes() != null && !expense.getNotes().isEmpty()) {
            desc.append("• Ghi chú: ").append(expense.getNotes()).append("\n");
        }

        desc.append("\nVui lòng xem xét và bỏ phiếu!");

        return desc.toString();
    }

    /**
     * Tạo options mặc định cho vote approval
     */
    private java.util.List<CreateVoteRequest.VoteOptionRequest> createDefaultApprovalOptions() {
        return java.util.List.of(
                CreateVoteRequest.VoteOptionRequest.builder()
                        .optionText("Đồng ý")
                        .description("Đồng ý phê duyệt")
                        .displayOrder(1)
                        .build(),
                CreateVoteRequest.VoteOptionRequest.builder()
                        .optionText("Không đồng ý")
                        .description("Không đồng ý phê duyệt")
                        .displayOrder(2)
                        .build(),
                CreateVoteRequest.VoteOptionRequest.builder()
                        .optionText("Cần thêm thông tin")
                        .description("Cần bổ sung thông tin trước khi quyết định")
                        .displayOrder(3)
                        .build()
        );
    }

    /**
     * Format tiền tệ
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0 VNĐ";
        }
        return String.format("%,.0f VNĐ", amount);
    }
}
