package ru.test.spring.boot_security.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.test.spring.boot_security.demo.entities.Request;
import ru.test.spring.boot_security.demo.entities.User;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByClient(User client);
    List<Request> findAll();
}
