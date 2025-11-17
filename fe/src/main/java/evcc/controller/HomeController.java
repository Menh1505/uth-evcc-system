package evcc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String dashboard(Model model) {
        model.addAttribute("title", "Dashboard - EVCC System");
        return "dashboard";
    }
} 
