package com.budongsan.api.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 웹 페이지 View 컨트롤러
 * @Controller → HTML 뷰 반환 (REST API가 아님)
 */
@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";  // templates/auth/login.html
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "auth/signup"; // templates/auth/signup.html
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";   // templates/dashboard.html
    }
}
