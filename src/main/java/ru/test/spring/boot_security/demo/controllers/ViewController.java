package ru.test.spring.boot_security.demo.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.test.spring.boot_security.demo.entities.Request;
import ru.test.spring.boot_security.demo.entities.Role;
import ru.test.spring.boot_security.demo.entities.User;
import ru.test.spring.boot_security.demo.services.RequestService;
import ru.test.spring.boot_security.demo.services.UserService;

import java.util.List;

@Controller
public class ViewController {

    private final UserService userService;
    private final RequestService requestService;

    public ViewController(UserService userService, RequestService requestService) {
        this.userService = userService;
        this.requestService = requestService;
    }

    @GetMapping("/")
    public String index(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findUserByName(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("role", user.getRole().name());
            return "index";
        }
        return "index";
    }

    @GetMapping("/auth/register")
    public String register() {
        return "register";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/requests/create")
    public String createRequest() {
        return "create-request";
    }

    @GetMapping("/requests/my-requests")
    public String getMyRequests(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        User client = userService.findUserByName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Request> requests = requestService.getRequestsByClient(client);
        model.addAttribute("requests", requests);
        return "my-requests";
    }

    @GetMapping("/requests/all-requests")
    public String getAllRequests(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        User operator = userService.findUserByName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (operator.getRole() != Role.OPERATOR) {
            throw new RuntimeException("Access denied");
        }
        List<Request> requests = requestService.getAllRequests();
        model.addAttribute("requests", requests);
        return "all-requests";
    }
}