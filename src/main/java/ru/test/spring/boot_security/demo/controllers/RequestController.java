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

import java.util.List;

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
    public String createRequest(@RequestParam String data, Authentication authentication, Model model) {
        System.out.println("Creating request with data: " + data + " by user: " + authentication.getName());
        User client = userService.findUserByName(authentication.getName())
                .orElseThrow(() -> {
                    System.out.println("User not found for name: " + authentication.getName());
                    return new RuntimeException("User not found");
                });
        requestService.createRequest(client, data);
        System.out.println("Request created successfully.");
        return "redirect:/requests/my-requests";
    }

//    @GetMapping("/{id}")
//    public String getRequest(@PathVariable Long id, Authentication authentication, Model model) {
//        User user = userService.findUserByName(authentication.getName())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        Request request = requestService.findRequestById(id)
//                .orElseThrow(() -> new RuntimeException("Request not found"));
//
//        if (user.getRole() == Role.CLIENT) {
//            return getClientView(request, model);
//        } else if (user.getRole() == Role.OPERATOR) {
//            return getOperatorView(request, model);
//        } else {
//            throw new RuntimeException("Invalid role");
//        }
//    }

    private String getClientView(Request request, Model model) {
        model.addAttribute("request", request);
        if (request.getStatus() == Status.ERROR) {
            return "client-request";
        } else {
            return "client-request"; // Используем тот же шаблон, но с другим содержимым
        }
    }

    private String getOperatorView(Request request, Model model) {
        model.addAttribute("request", request);
        if (request.getStatus() == Status.NEW || request.getStatus() == Status.FIXED) {
            return "operator-request";
        } else {
            return "operator-request"; // Используем тот же шаблон, но с другим содержимым
        }
    }

    @PostMapping("/update")
    public String updateClientRequest(@RequestParam Long id, @RequestParam String data, @RequestParam String comment, Authentication authentication) {
        User client = userService.findUserByName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Request request = requestService.findRequestById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getClient().equals(client)) {
            throw new RuntimeException("Access denied");
        }

        request.setData(data);
        request.setStatus(Status.FIXED);
        request.setComment(comment);
        requestService.updateRequestStatus(request, Status.FIXED, comment, null);

        return "redirect:/requests/my-requests";
    }

    @PostMapping("/update-status")
    public String updateOperatorRequest(@RequestParam Long id, @RequestParam String comment, @RequestParam String action, Authentication authentication) {
        User operator = userService.findUserByName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Request request = requestService.findRequestById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (action.equals("reject")) {
            request.setStatus(Status.ERROR);
            request.setComment(comment);
            requestService.updateRequestStatus(request, Status.ERROR, comment, operator);
        } else if (action.equals("accept")) {
            request.setStatus(Status.DONE);
            request.setComment(comment);
            requestService.updateRequestStatus(request, Status.DONE, comment, operator);
        }

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
    public String getAllRequests(Authentication authentication, Model model) {
        User operator = userService.findUserByName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (operator.getRole() != Role.OPERATOR) {
            throw new RuntimeException("Access denied");
        }
        List<Request> requests = requestService.getAllRequests();
        model.addAttribute("requests", requests);
        return "all-requests";
    }

    @GetMapping("/edit/{id}")
    public String editRequest(@PathVariable Long id, Authentication authentication, Model model) {
        User client = userService.findUserByName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Request request = requestService.findRequestById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getClient().equals(client)) {
            throw new RuntimeException("Access denied");
        }

        model.addAttribute("request", request);
        return "edit-request";
    }
}
