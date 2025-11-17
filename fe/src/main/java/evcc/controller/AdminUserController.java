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

import evcc.dto.response.UserLoginResponse;
import evcc.dto.response.UserProfileResponseDto;
import evcc.dto.response.UserStatsResponseDto;
import evcc.exception.ApiException;
import evcc.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showUserManagement(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập với quyền ADMIN.");
            return "redirect:/auth/login";
        }

        if (currentUser.getRoles() == null || !currentUser.getRoles().contains("ADMIN")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền truy cập trang quản trị người dùng.");
            return "redirect:/";
        }

        String token = currentUser.getToken();
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại.");
            session.invalidate();
            return "redirect:/auth/login";
        }

        List<UserProfileResponseDto> allUsers = List.of();
        List<UserProfileResponseDto> unverifiedUsers = List.of();
        UserStatsResponseDto stats = null;

        try {
            allUsers = userService.getAllUsers(token);
        } catch (ApiException e) {
            logger.warn("Không thể lấy danh sách tất cả user: {}", e.getErrorMessage());
        }

        try {
            unverifiedUsers = userService.getUnverifiedUsers(token);
        } catch (ApiException e) {
            logger.warn("Không thể lấy danh sách user chưa xác minh: {}", e.getErrorMessage());
        }

        try {
            stats = userService.getUserStats(token);
        } catch (ApiException e) {
            logger.warn("Không thể lấy thống kê user: {}", e.getErrorMessage());
        }

        model.addAttribute("title", "Quản trị người dùng - EVCC System");
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("unverifiedUsers", unverifiedUsers);
        model.addAttribute("stats", stats);
        return "admin/users";
    }

    @PostMapping("/{userId}/verify")
    public String verifyUser(@PathVariable String userId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập với quyền ADMIN.");
            return "redirect:/auth/login";
        }

        if (currentUser.getRoles() == null || !currentUser.getRoles().contains("ADMIN")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xác minh user.");
            return "redirect:/admin/users";
        }

        String token = currentUser.getToken();
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại.");
            session.invalidate();
            return "redirect:/auth/login";
        }

        try {
            UserProfileResponseDto verifiedUser = userService.verifyUser(token, userId);
            redirectAttributes.addFlashAttribute("successMessage",
                "Đã xác minh user " + verifiedUser.getUsername() + " thành công.");
            redirectAttributes.addFlashAttribute("contractUser", verifiedUser);
            redirectAttributes.addFlashAttribute("contractAdmin", currentUser.getUsername());
            return "redirect:/admin/users";
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            return "redirect:/admin/users";
        }
    }
}
