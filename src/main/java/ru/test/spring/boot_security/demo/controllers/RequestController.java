package ru.test.spring.boot_security.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.test.spring.boot_security.demo.entities.Request;
import ru.test.spring.boot_security.demo.entities.Status;
import ru.test.spring.boot_security.demo.entities.User;
import ru.test.spring.boot_security.demo.services.RequestService;
import ru.test.spring.boot_security.demo.services.UserService;

@Controller
@RequestMapping("/requests")
public class RequestController {

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

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

    @PostMapping("/update-status")
    public String updateOperatorRequest(@RequestParam("id") Long id,
                                        @RequestParam("comment") String comment,
                                        @RequestParam("action") String action,
                                        Authentication authentication,
                                        Model model) {
        try {
            logger.info("Updating status for request id: {}", id);
            logger.info("Action: {}", action);
            logger.info("Comment: {}", comment);

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
                return "operator-request";
            }
            model.addAttribute("request", request);
            return "redirect:/requests/all-requests";
        } catch (RuntimeException e) {
            // Если заявка не найдена, перенаправляем на страницу ошибки
            model.addAttribute("errorMessage", "Заявка с ID " + id + " не найдена.");
            return "error";
        }
    }

//    @GetMapping("/{id}")
//    public String getRequest(@PathVariable("id") Long id, Model model, Authentication authentication) {
//        System.out.println("Received request for ID: " + id);
//
//        Request request = requestService.findRequestById(id);
//
//        model.addAttribute("request", request);
//
//        return "operator-request";
//    }

//    private String getClientView(Request request, Model model) {
//        model.addAttribute("request", request);
//        if (request.getStatus() == Status.ERROR) {
//            return "edit-request";
//        } else {
//            return "client-request";
//        }
//    }
//
//    private String getOperatorView(Request request, Model model) {
//        model.addAttribute("request", request);
//        if (request.getStatus() == Status.NEW || request.getStatus() == Status.FIXED) {
//            return "operator-request";
//        } else {
//            return "operator-request";
//        }
//    }

}
