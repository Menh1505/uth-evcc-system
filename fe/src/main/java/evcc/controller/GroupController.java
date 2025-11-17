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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import evcc.dto.request.AddGroupMemberRequest;
import evcc.dto.request.CreateGroupRequest;
import evcc.dto.response.GroupResponseDto;
import evcc.dto.response.UserLoginResponse;
import evcc.exception.ApiException;
import evcc.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/groups")
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    private final UserService userService;

    public GroupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listMyGroups(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để quản lý nhóm.");
            return "redirect:/auth/login";
        }

        String token = currentUser.getToken();
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại.");
            session.invalidate();
            return "redirect:/auth/login";
        }

        try {
            List<GroupResponseDto> groups = userService.getMyGroups(token);
            model.addAttribute("title", "Nhóm của tôi - EVCC System");
            model.addAttribute("groups", groups);
            return "groups/list";
        } catch (ApiException e) {
            logger.error("Không thể lấy danh sách nhóm: {}", e.getErrorMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/create")
    public String createGroup(@RequestParam String name,
                              @RequestParam(required = false) String description,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để tạo nhóm.");
            return "redirect:/auth/login";
        }

        String token = currentUser.getToken();
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại.");
            session.invalidate();
            return "redirect:/auth/login";
        }

        try {
            CreateGroupRequest request = new CreateGroupRequest(name, description);
            GroupResponseDto group = userService.createGroup(token, request);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo nhóm \"" + group.getName() + "\" thành công.");
            return "redirect:/groups/" + group.getId();
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            return "redirect:/groups";
        }
    }

    @GetMapping("/{groupId}")
    public String groupDetail(@PathVariable Long groupId,
                              Model model,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để xem nhóm.");
            return "redirect:/auth/login";
        }

        String token = currentUser.getToken();
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại.");
            session.invalidate();
            return "redirect:/auth/login";
        }

        try {
            GroupResponseDto group = userService.getGroupDetail(token, groupId);
            var contracts = userService.getContractsByGroup(token, groupId);
            model.addAttribute("title", "Chi tiết nhóm - EVCC System");
            model.addAttribute("group", group);
            model.addAttribute("groupContracts", contracts);
            model.addAttribute("currentUsername", currentUser.getUsername());
            return "groups/detail";
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            return "redirect:/groups";
        }
    }

    @PostMapping("/{groupId}/members")
    public String addMember(@PathVariable Long groupId,
                            @RequestParam String userId,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để quản lý nhóm.");
            return "redirect:/auth/login";
        }
        String token = currentUser.getToken();
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại.");
            session.invalidate();
            return "redirect:/auth/login";
        }

        try {
            userService.addMemberToGroup(token, groupId, new AddGroupMemberRequest(userId));
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm thành viên vào nhóm.");
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
        }
        return "redirect:/groups/" + groupId;
    }

    @PostMapping("/{groupId}/members/{membershipId}/remove")
    public String removeMember(@PathVariable Long groupId,
                               @PathVariable Long membershipId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để quản lý nhóm.");
            return "redirect:/auth/login";
        }
        String token = currentUser.getToken();
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại.");
            session.invalidate();
            return "redirect:/auth/login";
        }

        try {
            userService.removeMemberFromGroup(token, groupId, membershipId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa thành viên khỏi nhóm.");
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
        }
        return "redirect:/groups/" + groupId;
    }
}
