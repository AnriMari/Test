package ru.test.spring.boot_security.demo.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.test.spring.boot_security.demo.entities.Request;
import ru.test.spring.boot_security.demo.entities.Status;
import ru.test.spring.boot_security.demo.entities.User;
import ru.test.spring.boot_security.demo.services.RequestService;
import ru.test.spring.boot_security.demo.services.UserService;

import java.util.List;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/requests/api")
public class RequestController {

    private final RequestService requestService;
    private final UserService userService;

    public RequestController(RequestService requestService, UserService userService) {
        this.requestService = requestService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public String createRequest(@RequestParam String data, Authentication authentication, Model model) {
        User client = userService.findUserByName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Request request = requestService.createRequest(client, data);
        model.addAttribute("request", request);
        return "redirect:/requests/my-requests";
    }

    @PostMapping("/update-status")
    public String updateRequestStatus(@RequestParam Long requestId, @RequestParam Status status,
                                      @RequestParam String comment, Authentication authentication, Model model) {
        User operator = userService.findUserByName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Request request = requestService.findRequestById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        requestService.updateRequestStatus(request, status, comment, operator);
        model.addAttribute("request", request);
        return "redirect:/requests/all-requests";
    }

    @GetMapping("/my-requests")
    public String getMyRequests(Authentication authentication, Model model) {
        User client = userService.findUserByName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Request> requests = requestService.getRequestsByClient(client);
        model.addAttribute("requests", requests);
        return "my-requests";
    }

    @GetMapping("/all-requests")
    public String getAllRequests(Model model) {
        List<Request> requests = requestService.getAllRequests();
        model.addAttribute("requests", requests);
        return "all-requests";
    }
}