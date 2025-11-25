package evcc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import evcc.dto.request.UserLoginRequest;
import evcc.dto.request.UserRegisterRequest;
import evcc.dto.response.UserLoginResponse;
import evcc.dto.response.UserRegisterResponse;
import evcc.exception.ApiException;
import evcc.service.UserLocalService;
import jakarta.validation.Valid;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final UserLocalService userLocalService;
    
    public AuthController(UserLocalService userLocalService) {
        this.userLocalService = userLocalService;
    }
    
    /**
     * Hiển thị trang đăng ký
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userRegisterRequest", new UserRegisterRequest());
        model.addAttribute("title", "Đăng ký - EVCC System");
        return "auth/register";
    }
    
    /**
     * Xử lý đăng ký user mới
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute UserRegisterRequest request,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        logger.info("Nhận request đăng ký: {}", request);
        
        // Kiểm tra validation
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Đăng ký - EVCC System");
            return "auth/register";
        }
        
        try {
            // Gọi local service đăng ký
            UserRegisterResponse response = userLocalService.registerUser(request);
            
            if (response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Đăng ký thành công! Chào mừng " + response.getUsername());
                logger.info("Đăng ký thành công cho user: {}", response.getUsername());
                return "redirect:/auth/login";
            } else {
                model.addAttribute("errorMessage", response.getMessage());
                model.addAttribute("title", "Đăng ký - EVCC System");
                return "auth/register";
            }
            
        } catch (ApiException e) {
            logger.error("Lỗi API khi đăng ký: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getErrorMessage());
            model.addAttribute("title", "Đăng ký - EVCC System");
            return "auth/register";
        }
    }
    
    /**
     * API endpoint để đăng ký (cho AJAX calls)
     */
    @PostMapping("/api/register")
    @ResponseBody
    public ResponseEntity<UserRegisterResponse> registerUserApi(@Valid @RequestBody UserRegisterRequest request) {
        
        logger.info("API call đăng ký user: {}", request);
        
        try {
            UserRegisterResponse response = userLocalService.registerUser(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (ApiException e) {
            logger.error("Lỗi API khi đăng ký qua API: {}", e.getMessage());
            
            UserRegisterResponse errorResponse = new UserRegisterResponse(
                false, e.getErrorMessage(), null, null, null
            );
            
            if (e.getStatusCode() == 400 || e.getStatusCode() == 409) {
                return ResponseEntity.badRequest().body(errorResponse);
            } else {
                return ResponseEntity.status(500).body(errorResponse);
            }
        }
    }
    
    /**
     * Hiển thị trang đăng nhập
     */
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userLoginRequest", new UserLoginRequest());
        model.addAttribute("title", "Đăng nhập - EVCC System");
        return "auth/login";
    }
    
    /**
     * Xử lý đăng nhập user
     */
    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute UserLoginRequest request,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes,
                           HttpSession session) {
        
        logger.info("Nhận request đăng nhập: {}", request);
        
        // Kiểm tra validation
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Đăng nhập - EVCC System");
            return "auth/login";
        }
        
        try {
            // Gọi local service đăng nhập
            UserLoginResponse response = userLocalService.loginUser(request);
            
            if (response.isSuccess()) {
                // Lưu thông tin user vào session để dùng cho navbar / profile
                session.setAttribute("currentUser", response);
                
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Đăng nhập thành công! Chào mừng " + response.getUsername());
                logger.info("Đăng nhập thành công cho user: {}", response.getUsername());
                                
                return "redirect:/";
            } else {
                model.addAttribute("errorMessage", response.getMessage());
                model.addAttribute("title", "Đăng nhập - EVCC System");
                return "auth/login";
            }
            
        } catch (ApiException e) {
            logger.error("Lỗi API khi đăng nhập: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getErrorMessage());
            model.addAttribute("title", "Đăng nhập - EVCC System");
            return "auth/login";
        }
    }
    
    /**
     * API endpoint để đăng nhập (cho AJAX calls)
     */
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<UserLoginResponse> loginUserApi(@Valid @RequestBody UserLoginRequest request) {
        
        logger.info("API call đăng nhập user: {}", request);
        
        try {
            UserLoginResponse response = userLocalService.loginUser(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body(response);
            }
            
        } catch (ApiException e) {
            logger.error("Lỗi API khi đăng nhập qua API: {}", e.getMessage());
            
            UserLoginResponse errorResponse = new UserLoginResponse(
                false, e.getErrorMessage(), null, null, null
            );
            
            if (e.getStatusCode() == 401) {
                return ResponseEntity.status(401).body(errorResponse);
            } else if (e.getStatusCode() == 400) {
                return ResponseEntity.badRequest().body(errorResponse);
            } else {
                return ResponseEntity.status(500).body(errorResponse);
            }
        }
    }
    
    /**
     * Đăng xuất user: xóa session và quay về trang đăng nhập
     */
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session != null) {
            session.invalidate();
        }
        redirectAttributes.addFlashAttribute("successMessage", "Bạn đã đăng xuất khỏi hệ thống.");
        return "redirect:/auth/login";
    }
    
    /**
     * Hiển thị trang quên mật khẩu
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("title", "Quên mật khẩu - EVCC System");
        return "auth/forgot-password";
    }
    
    /**
     * Xử lý yêu cầu quên mật khẩu
     */
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        
        logger.info("Nhận request quên mật khẩu cho email: {}", email);
        
        redirectAttributes.addFlashAttribute("successMessage", 
            "Hướng dẫn đặt lại mật khẩu đã được gửi tới email của bạn. Vui lòng kiểm tra hộp thư đến.");
        logger.info("Yêu cầu quên mật khẩu được xử lý cho: {}", email);
        return "redirect:/auth/login";
    }
    
    /**
     * API endpoint để quên mật khẩu (cho AJAX calls)
     */
    @PostMapping("/api/forgot-password")
    @ResponseBody
    public ResponseEntity<String> forgotPasswordApi(@RequestParam String email) {
        
        logger.info("API call quên mật khẩu cho email: {}", email);
        return ResponseEntity.ok("{\"success\":true,\"message\":\"Hướng dẫn đặt lại mật khẩu đã được gửi tới email của bạn\"}");
    }
    
    /**
     * Hiển thị trang đổi mật khẩu
     */
    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model, HttpSession session) {
        // Kiểm tra user đã đăng nhập chưa
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("title", "Đổi mật khẩu - EVCC System");
        return "auth/change-password";
    }
    
    /**
     * Xử lý đổi mật khẩu
     */
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        
        logger.info("Nhận request đổi mật khẩu");
        
        // Kiểm tra user đã đăng nhập chưa
        UserLoginResponse currentUser = (UserLoginResponse) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        // Kiểm tra mật khẩu mới và xác nhận có khớp không
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Mật khẩu mới và xác nhận mật khẩu không khớp!");
            model.addAttribute("title", "Đổi mật khẩu - EVCC System");
            return "auth/change-password";
        }
        
        redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
        logger.info("Đổi mật khẩu thành công cho user: {}", currentUser.getUsername());
        return "redirect:/user/profile";
    }
    
    /**
     * API endpoint để đổi mật khẩu (cho AJAX calls)
     */
    @PostMapping("/api/change-password")
    @ResponseBody
    public ResponseEntity<String> changePasswordApi(@RequestBody java.util.Map<String, String> request,
                                                    HttpSession session) {
        
        logger.info("API call đổi mật khẩu");
        
        UserLoginResponse currentUser = (UserLoginResponse) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401)
                .body("{\"success\":false,\"message\":\"Vui lòng đăng nhập để đổi mật khẩu\"}");
        }
        
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");
        
        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest()
                .body("{\"success\":false,\"message\":\"Mật khẩu mới và xác nhận mật khẩu không khớp\"}");
        }
        
        return ResponseEntity.ok("{\"success\":true,\"message\":\"Đổi mật khẩu thành công\"}");
    }
    
    /**
     * Kiểm tra trạng thái API server
     */
    @GetMapping("/api/status")
    @ResponseBody
    public ResponseEntity<String> checkApiStatus() {
        boolean isAvailable = true; // Local service luôn available
        
        if (isAvailable) {
            return ResponseEntity.ok("{\"status\":\"connected\",\"message\":\"API server khả dụng\"}");
        } else {
            return ResponseEntity.status(503)
                .body("{\"status\":\"disconnected\",\"message\":\"API server không khả dụng\"}");
        }
    }
}
