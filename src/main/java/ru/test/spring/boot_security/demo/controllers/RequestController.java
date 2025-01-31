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
        User client = userService.findUserByName(authentication.getName())
                .orElseThrow(() -> {
                    System.out.println("User not found for name: " + authentication.getName());
                    return new RuntimeException("User not found");
                });
        requestService.createRequest(client, data);
        return "redirect:/requests/my-requests";
    }

    @GetMapping("/{id}")
    public String getRequest(@PathVariable("id") Long id, Authentication authentication, Model model) {
        User user = userService.findUserByName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Request request = requestService.findRequestById(id);

        if (user.getRole() == Role.CLIENT) {
            return getClientView(request, model);
        } else if (user.getRole() == Role.OPERATOR) {
            return getOperatorView(request, model);
        } else {
            throw new RuntimeException("Invalid role");
        }
    }

    private String getClientView(Request request, Model model) {
        model.addAttribute("request", request);
        if (request.getStatus() == Status.ERROR) {
            return "edit-request";
        } else {
            return "client-request";
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
        Request request = requestService.findRequestById(id);

        if (!request.getClient().equals(client)) {
            throw new RuntimeException("Access denied");
        }

        request.setData(data);
        request.setStatus(Status.FIXED);
        request.setComment(comment);
        requestService.updateRequestStatus(request, Status.FIXED, comment, null);

        return "redirect:/requests/my-requests";
    }
//вергуть
//    @PostMapping("/update-status")
//    public String updateOperatorRequest(@RequestParam(name = "id", required = false) String idStr,
//                                        @RequestParam(name = "comment", required = false) String comment,
//                                        @RequestParam(name = "action", required = false) String action,
//                                        Authentication authentication) {
//        System.out.println("Received parameters: id=" + idStr + ", comment=" + comment + ", action=" + action);
//
//        if (idStr == null || idStr.isEmpty()) {
//            throw new IllegalArgumentException("Ошибка: ID заявки отсутствует!");
//        }
//
//        Long id;
//        try {
//            id = Long.parseLong(idStr);
//        } catch (NumberFormatException e) {
//            throw new IllegalArgumentException("Ошибка: Неверный формат ID!");
//        }
//
//        User operator = userService.findUserByName(authentication.getName())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Request request = requestService.findRequestById(id);
//
//        System.out.println("Processing request ID: " + id + " by operator: " + operator.getName());
//
//        if (action == null || (!action.equals("reject") && !action.equals("accept"))) {
//            throw new IllegalArgumentException("Ошибка: Некорректное действие!");
//        }
//
//        if ("reject".equals(action)) {
//            requestService.updateRequestStatus(request, Status.ERROR, comment, operator);
//        } else if ("accept".equals(action)) {
//            requestService.updateRequestStatus(request, Status.DONE, comment, operator);
//        }
//
//        return "redirect:/requests/all-requests";
//    }

    @PostMapping("/update-status")
    public String updateOperatorRequest(@RequestParam("id") Long id,
                                        @RequestParam("comment") String comment,
                                        @RequestParam("action") String action,
                                        Authentication authentication, Model model) {
        System.out.println("Update status request with ID: " + id + ", comment: " + comment + ", action: " + action);
        User operator = userService.findUserByName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Request request = requestService.findRequestById(id);
        System.out.println(request);

        if (action.equals("reject")) {
            request.setStatus(Status.ERROR);
            request.setComment(comment);
            requestService.updateRequestStatus(request, Status.ERROR, comment, operator);
        } else if (action.equals("accept")) {
            request.setStatus(Status.DONE);
            request.setComment(comment);
            requestService.updateRequestStatus(request, Status.DONE, comment, operator);
        } else {
            model.addAttribute("error", "Invalid action");
            return "operator-request"; // Возвращаемся на форму с сообщением об ошибке
        }

        return "redirect:/requests/all-requests";
    }

    @GetMapping("/my-requests")
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

    @GetMapping("/all-requests")
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


    @GetMapping("/edit/{id}")
    public String editRequest(@PathVariable("id") Long id, Authentication authentication, Model model) {
        User client = userService.findUserByName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Request request = requestService.findRequestById(id);

        if (!request.getClient().equals(client)) {
            throw new RuntimeException("Access denied");
        }

        model.addAttribute("request", request);
        return "edit-request";
    }
}
