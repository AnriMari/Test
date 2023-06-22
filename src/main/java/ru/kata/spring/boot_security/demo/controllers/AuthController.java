package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.UserServiceImpl;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.validate.UserValidator;

import javax.validation.Valid;

@Controller
public class AuthController {

    private final UserValidator userValidator;
    private final UserServiceImpl userServiceImpl;
    private final RoleService roleService;

    @Autowired
    public AuthController(UserValidator userValidator, UserServiceImpl userServiceImpl, RoleService roleService) {
        this.userValidator = userValidator;
        this.userServiceImpl = userServiceImpl;
        this.roleService = roleService;
    }

    @GetMapping("/login")
    public String indexPage() {
        return "login";
    }

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/registration")
    public String userRegistrationPage(@ModelAttribute("user") User user, Model role) {
        role.addAttribute("rolesList", roleService.getRolesList());
        return "registration";
    }

    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("user") @Valid User user,
                                      BindingResult bindingResult){

        userValidator.validate(user, bindingResult);
        if(bindingResult.hasErrors()) {
            return "registration";
        }
        userServiceImpl.userRegistration(user);

        return "redirect:/login";
    }


}
