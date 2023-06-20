package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.services.RoleService;

import java.security.Principal;


@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping()
    public String adminPage(Model model, Model role, Principal principal) {
        role.addAttribute("rolesList", roleService.getRolesList());
        model.addAttribute("users", userService.showAllUsers());
        User princ = userService.getUserByUsername(principal.getName());
        model.addAttribute("princ", princ);
        model.addAttribute("newUser", new User());
        return "adminpage";
    }

    @GetMapping("/user")
    public String showUsersInfo(Model model, Principal principal) {
        model.addAttribute("user",userService.getUserByUsername(principal.getName()));
        return "user";
    }


    @GetMapping("/user/{id}")
    public String userPage(Model model, @PathVariable("id") Long id) {
        model.addAttribute("user", userService.getUserById(id));
        return "user";
    }

    @PostMapping("/new")
    public String add(@ModelAttribute("user") User user){
        userService.addUser(user);
        return "redirect:/admin";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("user") User user,
                         @PathVariable("id") Long id) {
        userService.editUser(id, user);
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

}
