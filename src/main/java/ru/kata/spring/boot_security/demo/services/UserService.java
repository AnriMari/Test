package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.entities.User;

import java.util.List;

public interface UserService {

    List<User> showAllUsers();

    void addUser(User user);

    User getUserById(Long id);

    User getUserByUsername(String username);

    void deleteUser(Long id);

    void editUser(Long id, User updatedUser);

}
