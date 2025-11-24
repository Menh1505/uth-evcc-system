package evcc.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import evcc.dto.response.GroupResponseDto;
import evcc.dto.response.UserLoginResponse;
import evcc.dto.response.VoteResponseDto;
import evcc.exception.ApiException;
import evcc.service.GroupService;
import evcc.service.VotingService;
import jakarta.servlet.http.HttpSession;

/**
 * Frontend controller cho voting system
 */
@Controller
@RequestMapping("/votes")
public class VotingPageController {

    private static final Logger logger = LoggerFactory.getLogger(VotingPageController.class);

    private final VotingService votingService;
    private final GroupService groupService;

    public VotingPageController(VotingService votingService, GroupService groupService) {
        this.votingService = votingService;
        this.groupService = groupService;
    }

    /**
     * Trang danh sách vote của nhóm
     */
    @GetMapping("/group/{groupId}")
    public String listGroupVotes(@PathVariable Long groupId,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserLoginResponse currentUser = (UserLoginResponse) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            // Lấy thông tin nhóm
            GroupResponseDto group = groupService.getGroupDetail(groupId, currentUser.getToken());

            // Lấy danh sách vote
            List<VoteResponseDto> votes = votingService.getGroupVotes(groupId, currentUser.getToken());
            List<VoteResponseDto> pendingVotes = votingService.getPendingVotes(groupId, currentUser.getToken());

            model.addAttribute("title", "Bỏ phiếu nhóm: " + group.getName());
            model.addAttribute("group", group);
            model.addAttribute("votes", votes);
            model.addAttribute("pendingVotes", pendingVotes);
            model.addAttribute("currentUser", currentUser);

            return "votes/group-votes";

        } catch (ApiException e) {
            logger.error("Lỗi khi lấy danh sách vote nhóm {}: {}", groupId, e.getErrorMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            return "redirect:/groups/" + groupId;
        }
    }

    /**
     * Trang chi tiết vote
     */
    @GetMapping("/{voteId}")
    public String voteDetail(@PathVariable Long voteId,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserLoginResponse currentUser = (UserLoginResponse) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            VoteResponseDto vote = votingService.getVoteDetail(voteId, currentUser.getToken());

            model.addAttribute("title", "Chi tiết vote: " + vote.getTitle());
            model.addAttribute("vote", vote);
            model.addAttribute("currentUser", currentUser);

            return "votes/vote-detail";

        } catch (ApiException e) {
            logger.error("Lỗi khi lấy chi tiết vote {}: {}", voteId, e.getErrorMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể tải chi tiết vote");
            return "redirect:/";
        }
    }

    /**
     * Trang tạo vote mới
     */
    @GetMapping("/group/{groupId}/create")
    public String createVotePage(@PathVariable Long groupId,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserLoginResponse currentUser = (UserLoginResponse) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            GroupResponseDto group = groupService.getGroupDetail(groupId, currentUser.getToken());

            model.addAttribute("title", "Tạo vote mới - " + group.getName());
            model.addAttribute("group", group);
            model.addAttribute("currentUser", currentUser);

            return "votes/create-vote";

        } catch (ApiException e) {
            logger.error("Lỗi khi tải trang tạo vote cho nhóm {}: {}", groupId, e.getErrorMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            return "redirect:/groups/" + groupId;
        }
    }

    /**
     * Xử lý cast vote
     */
    @PostMapping("/{voteId}/cast")
    public String castVote(@PathVariable Long voteId,
            String optionId,
            String comment,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserLoginResponse currentUser = (UserLoginResponse) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            votingService.castVote(voteId, Long.parseLong(optionId), comment, currentUser.getToken());
            redirectAttributes.addFlashAttribute("successMessage", "Bỏ phiếu thành công!");

        } catch (ApiException e) {
            logger.error("Lỗi khi cast vote {}: {}", voteId, e.getErrorMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lựa chọn không hợp lệ");
        }

        return "redirect:/votes/" + voteId;
    }

    /**
     * Bắt đầu vote
     */
    @PostMapping("/{voteId}/start")
    public String startVote(@PathVariable Long voteId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserLoginResponse currentUser = (UserLoginResponse) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            votingService.startVote(voteId, currentUser.getToken());
            redirectAttributes.addFlashAttribute("successMessage", "Đã bắt đầu vote!");

        } catch (ApiException e) {
            logger.error("Lỗi khi start vote {}: {}", voteId, e.getErrorMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
        }

        return "redirect:/votes/" + voteId;
    }

    /**
     * Kết thúc vote
     */
    @PostMapping("/{voteId}/close")
    public String closeVote(@PathVariable Long voteId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserLoginResponse currentUser = (UserLoginResponse) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            votingService.closeVote(voteId, currentUser.getToken());
            redirectAttributes.addFlashAttribute("successMessage", "Đã kết thúc vote và tính kết quả!");

        } catch (ApiException e) {
            logger.error("Lỗi khi close vote {}: {}", voteId, e.getErrorMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
        }

        return "redirect:/votes/" + voteId;
    }
}
