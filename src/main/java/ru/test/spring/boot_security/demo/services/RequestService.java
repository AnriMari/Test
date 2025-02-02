package ru.test.spring.boot_security.demo.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.test.spring.boot_security.demo.entities.Request;
import ru.test.spring.boot_security.demo.entities.Status;
import ru.test.spring.boot_security.demo.entities.User;
import ru.test.spring.boot_security.demo.repository.RequestRepository;

import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    @Autowired
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

    public Request updateRequestStatus(Request request,
                                       Status status,
                                       String comment,
                                       User operator) {
        request.setStatus(status);
        request.setComment(comment);
        if (operator != null) {
            request.setOperator(operator);
            System.out.println("Operator: " + operator.getName());
        }
        return requestRepository.save(request);
    }

    public List<Request> getRequestsByClient(User client) {
        return requestRepository.findAllByClient(client);
    }

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public Request findRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }

    public Request updateRequest(Request request) {
        return requestRepository.save(request);
    }
}
