package ru.test.spring.boot_security.demo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.test.spring.boot_security.demo.entities.Role;
import ru.test.spring.boot_security.demo.entities.User;
import ru.test.spring.boot_security.demo.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String name, String password, Role role) {
        logger.info("Creating user with name: {}", name);
        User user = new User();
        user.setName(name); // Используем setName
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        User savedUser = userRepository.save(user);
        logger.info("User created successfully: {}", savedUser);
        return savedUser;
    }

    public Optional<User> findUserByName(String name) {
        logger.info("Finding user by name: {}", name);
        return userRepository.findByName(name); // Используем findByName
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        logger.info("Loading user by name: {}", name);
        return userRepository.findByName(name)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getName())
                        .password(user.getPasswordHash())
                        .roles(user.getRole().name())
                        .build())
                .orElseThrow(() -> {
                    logger.warn("User not found for name: {}", name);
                    return new UsernameNotFoundException("User not found");
                });
    }
}
