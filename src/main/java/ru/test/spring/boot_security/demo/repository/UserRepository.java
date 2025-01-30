package ru.test.spring.boot_security.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.test.spring.boot_security.demo.entities.User;

import java.util.Optional;

@Repository


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
}
