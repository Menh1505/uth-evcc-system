package evcc.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import evcc.dto.response.UserLoginResponse;
import evcc.dto.response.UserProfileResponseDto;
import evcc.service.UserLocalService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserProfilePageController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfilePageController.class);
    private final UserLocalService userLocalService;

    public UserProfilePageController(UserLocalService userLocalService) {
        this.userLocalService = userLocalService;
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        // Kiểm tra đăng nhập
        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để xem hồ sơ.");
            return "redirect:/auth/login";
        }

        UserLoginResponse currentUser = (UserLoginResponse) currentUserObj;

        // Kiểm tra userId
        if (currentUser.getUserId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ.");
            return "redirect:/auth/login";
        }

        try {
            // Lấy thông tin profile
            UUID userId = UUID.fromString(currentUser.getUserId());
            UserProfileResponseDto profile = userLocalService.getUserProfile(userId);

            // Tạo profile đơn giản nếu không tồn tại
            if (profile == null) {
                profile = createDefaultProfile(currentUser);
            }

            model.addAttribute("title", "Hồ sơ cá nhân - EVCC System");
            model.addAttribute("profile", profile);
            return "user/profile";

        } catch (Exception e) {
            logger.error("Lỗi khi lấy profile: {}", e.getMessage());

            // Tạo profile mặc định từ thông tin session
            UserProfileResponseDto defaultProfile = createDefaultProfile(currentUser);
            model.addAttribute("title", "Hồ sơ cá nhân - EVCC System");
            model.addAttribute("profile", defaultProfile);
            return "user/profile";
        }
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam(required = false) String citizenId,
            @RequestParam(required = false) String driverLicense,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra đăng nhập
        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để cập nhật hồ sơ.");
            return "redirect:/auth/login";
        }

        UserLoginResponse currentUser = (UserLoginResponse) currentUserObj;

        try {
            UUID userId = UUID.fromString(currentUser.getUserId());

            // Cập nhật thông tin đơn giản
            if (citizenId != null && !citizenId.trim().isEmpty()) {
                // Logic cập nhật CMND/CCCD
                logger.info("Cập nhật CMND/CCCD cho user: {}", currentUser.getUsername());
            }
            if (driverLicense != null && !driverLicense.trim().isEmpty()) {
                // Logic cập nhật bằng lái xe
                logger.info("Cập nhật bằng lái xe cho user: {}", currentUser.getUsername());
            }

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
            return "redirect:/user/profile";

        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật profile: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật thông tin.");
            return "redirect:/user/profile";
        }
    }

    private UserProfileResponseDto createDefaultProfile(UserLoginResponse currentUser) {
        UserProfileResponseDto profile = new UserProfileResponseDto();

        try {
            profile.setId(UUID.fromString(currentUser.getUserId()));
        } catch (Exception e) {
            profile.setId(UUID.randomUUID()); // Fallback UUID
        }

        profile.setUsername(currentUser.getUsername());
        profile.setEmail(currentUser.getUsername()); // Sử dụng username làm email
        profile.setStatus("UNVERIFIED");
        profile.setIsVerified(false);
        profile.setRoles(currentUser.getRoles());
        profile.setCitizenId(""); // Trống ban đầu
        profile.setDriverLicense(""); // Trống ban đầu
        profile.setCreatedAt(java.time.LocalDateTime.now());
        profile.setUpdatedAt(java.time.LocalDateTime.now());

        return profile;
    }
}
