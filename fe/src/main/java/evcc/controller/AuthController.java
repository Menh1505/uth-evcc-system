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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import evcc.dto.request.UserRegisterRequest;
import evcc.dto.response.UserRegisterResponse;
import evcc.exception.ApiException;
import evcc.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
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
            // Gọi API đăng ký
            UserRegisterResponse response = userService.registerUser(request);
            
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
            UserRegisterResponse response = userService.registerUser(request);
            
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
     * Kiểm tra trạng thái API server
     */
    @GetMapping("/api/status")
    @ResponseBody
    public ResponseEntity<String> checkApiStatus() {
        boolean isAvailable = userService.isApiServerAvailable();
        
        if (isAvailable) {
            return ResponseEntity.ok("{\"status\":\"connected\",\"message\":\"API server khả dụng\"}");
        } else {
            return ResponseEntity.status(503)
                .body("{\"status\":\"disconnected\",\"message\":\"API server không khả dụng\"}");
        }
    }
}