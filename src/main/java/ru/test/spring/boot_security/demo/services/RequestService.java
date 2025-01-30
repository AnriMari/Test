package ru.test.spring.boot_security.demo.services;


import org.springframework.stereotype.Service;
import ru.test.spring.boot_security.demo.entities.Request;
import ru.test.spring.boot_security.demo.entities.Status;
import ru.test.spring.boot_security.demo.entities.User;
import ru.test.spring.boot_security.demo.repository.RequestRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public Request createRequest(User client, String data) {
        Request request = new Request();
        request.setClient(client);
        request.setData(data);
        request.setStatus(Status.NEW);
        return requestRepository.save(request);
    }

    public Request updateRequestStatus(Request request, Status status, String comment, User operator) {
        request.setStatus(status);
        request.setComment(comment);
        request.setOperator(operator);
        return requestRepository.save(request);
    }

    public List<Request> getRequestsByClient(User client) {
        return requestRepository.findAllByClient(client);
    }

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public Optional<Request> findRequestById(Long id) {
        return requestRepository.findById(id);
    }
}
