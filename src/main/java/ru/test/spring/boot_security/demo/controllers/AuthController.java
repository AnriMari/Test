package ru.test.spring.boot_security.demo.controllers;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Возвращаем имя шаблона для формы входа
    }

    @PostMapping("/login")
    public String login(@RequestParam String name, @RequestParam String password, Model model) {
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(name, password);
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String password,
                           @RequestParam Role role,
                           Model model) {

        try {
            if (userService.findUserByName(name).isPresent()) {
                model.addAttribute("error", "Username already exists");
                return "register";
            } else {
                userService.createUser(name, password, role);
                model.addAttribute("message", "User registered successfully!");
                return "redirect:/login";
            }
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