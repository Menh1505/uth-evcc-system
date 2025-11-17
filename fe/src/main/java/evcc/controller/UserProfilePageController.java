package evcc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import evcc.dto.request.UpdateUserProfileRequest;
import evcc.dto.response.UserLoginResponse;
import evcc.dto.response.UserProfileResponseDto;
import evcc.exception.ApiException;
import evcc.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserProfilePageController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfilePageController.class);

    private final UserService userService;

    public UserProfilePageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để xem hồ sơ.");
            return "redirect:/auth/login";
        }

        String token = currentUser.getToken();
        if (token == null || token.isBlank()) {
            logger.warn("Không tìm thấy token trong session cho user {}", currentUser.getUsername());
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại.");
            session.invalidate();
            return "redirect:/auth/login";
        }

        try {
            UserProfileResponseDto profile = userService.getUserProfile(token);
            model.addAttribute("title", "Hồ sơ cá nhân - EVCC System");
            model.addAttribute("profile", profile);
            return "user/profile";
        } catch (ApiException e) {
            logger.error("Không thể tải hồ sơ user: {}", e.getErrorMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            session.invalidate();
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam(required = false) String citizenId,
                                @RequestParam(required = false) String driverLicense,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để cập nhật hồ sơ.");
            return "redirect:/auth/login";
        }

        String token = currentUser.getToken();
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại.");
            session.invalidate();
            return "redirect:/auth/login";
        }

        UpdateUserProfileRequest request = new UpdateUserProfileRequest(
            citizenId != null && !citizenId.isBlank() ? citizenId.trim() : null,
            driverLicense != null && !driverLicense.isBlank() ? driverLicense.trim() : null
        );

        try {
            userService.updateUserProfile(token, request);
            redirectAttributes.addFlashAttribute("successMessage",
                "Yêu cầu cập nhật/thẩm định thông tin cá nhân của bạn đã được gửi, vui lòng chờ admin duyệt.");
            return "redirect:/user/profile";
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            return "redirect:/user/profile";
        }
    }
}
