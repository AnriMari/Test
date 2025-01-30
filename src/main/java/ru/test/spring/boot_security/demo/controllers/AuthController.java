package ru.test.spring.boot_security.demo.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.test.spring.boot_security.demo.entities.Role;
import ru.test.spring.boot_security.demo.services.UserService;


@Controller
@RequestMapping("/auth")

public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String password, @RequestParam Role role, Model model) {
        try {
            userService.createUser(name, password, role);
            model.addAttribute("message", "User registered successfully!");
            return "redirect:/login"; // Перенаправляем на страницу входа после успешной регистрации
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/current-user")
    public String getCurrentUser(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("currentUser", authentication.getName());
        } else {
            model.addAttribute("currentUser", "No user logged in");
        }
        return "index";
    }
}