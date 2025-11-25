package evcc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import evcc.dto.response.GroupResponseDto;
import evcc.dto.response.UserLoginResponse;
import evcc.dto.local.LocalContract;
import evcc.service.GroupLocalService;
import evcc.service.UserLocalService;
import evcc.service.ContractLocalService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/groups")
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    private final GroupLocalService groupLocalService;
    private final UserLocalService userLocalService;
    private final ContractLocalService contractLocalService;

    public GroupController(GroupLocalService groupLocalService, UserLocalService userLocalService, ContractLocalService contractLocalService) {
        this.groupLocalService = groupLocalService;
        this.userLocalService = userLocalService;
        this.contractLocalService = contractLocalService;
    }

    private UserLoginResponse requireLogin(HttpSession session, RedirectAttributes redirectAttributes) {
        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để quản lý nhóm.");
            return null;
        }
        if (currentUser.getToken() == null || currentUser.getToken().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại.");
            session.invalidate();
            return null;
        }
        return currentUser;
    }

    @GetMapping
    public String listMyGroups(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        UserLoginResponse currentUser = requireLogin(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            UUID currentUserId = UUID.fromString(currentUser.getUserId());
            List<GroupResponseDto> groups = groupLocalService.getMyGroups(currentUserId);

            model.addAttribute("title", "Nhóm của tôi - EVCC System");
            model.addAttribute("groups", groups);
            return "groups/list";
        } catch (Exception e) {
            logger.error("Không thể lấy danh sách nhóm: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lấy danh sách nhóm: " + e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/{groupId}")
    public String groupDetail(@PathVariable Long groupId,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        UserLoginResponse currentUser = requireLogin(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            GroupResponseDto group = groupLocalService.getGroupDetail(groupId);
            if (group == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhóm với ID: " + groupId);
                return "redirect:/groups";
            }

            // Lấy danh sách hợp đồng của nhóm này với defensive check
            List<LocalContract> groupContracts = new ArrayList<>();
            try {
                groupContracts = contractLocalService.getContractsByGroup(groupId);
                if (groupContracts == null) {
                    groupContracts = new ArrayList<>();
                }
            } catch (Exception contractEx) {
                logger.warn("Không thể lấy danh sách hợp đồng cho nhóm {}: {}", groupId, contractEx.getMessage());
                groupContracts = new ArrayList<>(); // Fallback to empty list
            }

            model.addAttribute("title", "Chi tiết nhóm - EVCC System");
            model.addAttribute("group", group);
            model.addAttribute("groupContracts", groupContracts);
            return "groups/detail";
        } catch (Exception e) {
            logger.error("Không thể lấy thông tin nhóm {}: {}", groupId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể lấy thông tin nhóm: " + e.getMessage());
            return "redirect:/groups";
        }
    }

    @PostMapping("/create")
    public String createGroup(@RequestParam String name,
            @RequestParam(required = false) String description,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserLoginResponse currentUser = requireLogin(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            if (name == null || name.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tên nhóm không được để trống.");
                return "redirect:/groups";
            }

            UUID creatorId = UUID.fromString(currentUser.getUserId());
            GroupResponseDto group = groupLocalService.createGroup(
                    name.trim(),
                    description != null ? description.trim() : "",
                    creatorId,
                    currentUser.getUsername()
            );

            redirectAttributes.addFlashAttribute("successMessage",
                    "Tạo nhóm \"" + group.getName() + "\" thành công!");
            return "redirect:/groups/" + group.getId();
        } catch (Exception e) {
            logger.error("Lỗi khi tạo nhóm: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tạo nhóm: " + e.getMessage());
            return "redirect:/groups";
        }
    }

    @PostMapping("/{groupId}/members/add")
    public String addMember(@PathVariable Long groupId,
            @RequestParam String userId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserLoginResponse currentUser = requireLogin(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            UUID userIdUuid = UUID.fromString(userId);

            // Lấy thông tin user để có username
            String username = "Unknown User";
            try {
                var userProfile = userLocalService.getUserProfile(userIdUuid);
                if (userProfile != null && userProfile.getUsername() != null) {
                    username = userProfile.getUsername();
                }
            } catch (Exception userEx) {
                logger.warn("Không thể lấy username cho userId: {}", userId);
            }

            groupLocalService.addMemberToGroup(groupId, userIdUuid, username);

            redirectAttributes.addFlashAttribute("successMessage", "Thêm thành viên thành công!");
            return "redirect:/groups/" + groupId;
        } catch (Exception e) {
            logger.error("Lỗi khi thêm thành viên vào nhóm {}: {}", groupId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm thành viên: " + e.getMessage());
            return "redirect:/groups/" + groupId;
        }
    }

    @PostMapping("/{groupId}/members/{membershipId}/remove")
    public String removeMember(@PathVariable Long groupId,
            @PathVariable Long membershipId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserLoginResponse currentUser = requireLogin(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            groupLocalService.removeMemberFromGroup(groupId, membershipId);

            redirectAttributes.addFlashAttribute("successMessage", "Xóa thành viên thành công!");
            return "redirect:/groups/" + groupId;
        } catch (Exception e) {
            logger.error("Lỗi khi xóa thành viên khỏi nhóm {}: {}", groupId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa thành viên: " + e.getMessage());
            return "redirect:/groups/" + groupId;
        }
    }
}
