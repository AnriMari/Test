package ru.test.spring.boot_security.demo.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.test.spring.boot_security.demo.entities.Request;
import ru.test.spring.boot_security.demo.entities.Role;
import ru.test.spring.boot_security.demo.entities.Status;
import ru.test.spring.boot_security.demo.entities.User;
import ru.test.spring.boot_security.demo.services.RequestService;
import ru.test.spring.boot_security.demo.services.UserService;

@Controller
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;
    private final UserService userService;

    public RequestController(RequestService requestService, UserService userService) {
        this.requestService = requestService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public String createRequest(@RequestParam String data,
                                Authentication authentication,
                                Model model) {
        User client = userService.findUserByName(authentication.getName())
                .orElseThrow(() -> {
                    System.out.println("User not found for name: " + authentication.getName());
                    return new RuntimeException("User not found");
                });
        requestService.createRequest(client, data);
        return "redirect:/requests/my-requests";
    }

    @GetMapping("/update-status/{id}")
    public String showUpdateStatusForm(@PathVariable Long id, Authentication authentication, Model model) {
        try {
            User operator = userService.findUserByName(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Request request = requestService.findRequestById(id);

            if (request.getStatus() == Status.NEW || request.getStatus() == Status.FIXED) {
                model.addAttribute("request", request);
                return "update-status-form";
            } else {
                model.addAttribute("status", request.getStatus());
                return "request-status";
            }
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Заявка с ID " + id + " не найдена.");
            return "error";
        }
    }

    @PostMapping("/update-status/{id}")
    public String updateRequestStatus(@PathVariable Long id,
                                      @RequestParam("comment") String comment,
                                      @RequestParam("action") String action,
                                      Authentication authentication,
                                      Model model) {
        try {
            User operator = userService.findUserByName(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Request request = requestService.findRequestById(id);

            if (action.equals("reject")) {
                requestService.updateRequestStatus(request, Status.ERROR, comment, operator);
            } else if (action.equals("accept")) {
                requestService.updateRequestStatus(request, Status.DONE, comment, operator);
            } else if (action.equals("cancel")) {
            }

            return "redirect:/requests/all-requests";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Заявка с ID " + id + " не найдена.");
            return "error";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Authentication authentication, Model model) {
        try {
            User client = userService.findUserByName(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Request request = requestService.findRequestById(id);

            if (request.getStatus() == Status.ERROR) {
                model.addAttribute("request", request);
                return "edit-request-form";
            } else {
                model.addAttribute("status", request.getStatus());
                return "request-status";
            }
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Заявка с ID " + id + " не найдена.");
            return "error";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateRequest(@PathVariable Long id,
                                @RequestParam("data") String data,
                                Authentication authentication,
                                Model model) {
        try {
            User client = userService.findUserByName(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Request request = requestService.findRequestById(id);

            if (request.getStatus() == Status.ERROR) {
                request.setData(data);
                request.setStatus(Status.FIXED);
                requestService.updateRequest(request);
            }

            return "redirect:/requests/my-requests";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Заявка с ID " + id + " не найдена.");
            return "error";
        }
    }

    @GetMapping("/status/{id}")
    public String showRequestStatus(@PathVariable Long id, Authentication authentication, Model model) {
        try {
            User user = userService.findUserByName(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Request request = requestService.findRequestById(id);
            model.addAttribute("status", request.getStatus());
            model.addAttribute("role", user.getRole().name());
            if (user.getRole() == Role.OPERATOR && request.getStatus() == Status.ERROR) {
                model.addAttribute("request", request);

            } else if (user.getRole() == Role.CLIENT && request.getStatus() != Status.ERROR) {
                model.addAttribute("request", request);

            }
            return "request-status";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Заявка с ID " + id + " не найдена.");
            return "error";
        }
    }
}
