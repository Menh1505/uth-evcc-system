package evcc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class HomeController {
    
    @GetMapping
    public String index(Model model) {
        model.addAttribute("title", "EVCC System Frontend");
        model.addAttribute("message", "Welcome to EVCC Management System");
        return "index";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        model.addAttribute("title", "Dashboard - EVCC System");
        
        // Kiểm tra user đã đăng nhập
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("currentUser", currentUser);
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        
        return "dashboard";
    }
    
    /**
     * Hiển thị trang thông báo nhóm
     */
    @GetMapping("/group-notifications")
    public String groupNotifications(Model model, HttpSession session) {
        // Kiểm tra user đã đăng nhập
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("title", "Thông báo nhóm - EVCC System");
        return "notifications/group-notifications";
    }
    
    /**
     * Hiển thị trang quản lý thông báo
     */
    @GetMapping("/notifications")
    public String notifications(Model model, HttpSession session) {
        // Kiểm tra user đã đăng nhập
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("title", "Thông báo - EVCC System");
        return "notifications/notifications";
    }
}
