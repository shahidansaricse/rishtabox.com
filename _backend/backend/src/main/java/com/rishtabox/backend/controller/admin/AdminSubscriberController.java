package com.rishtabox.backend.controller.admin;

import com.rishtabox.backend.entity.Subscriber;
import com.rishtabox.backend.repository.SubscriberRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/subscribers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSubscriberController {

    private final SubscriberRepository subscriberRepository;

    public AdminSubscriberController(
            SubscriberRepository subscriberRepository) {

        this.subscriberRepository = subscriberRepository;
    }

    // Get all subscribers
    @GetMapping
    public ResponseEntity<List<Subscriber>> getAllSubscribers() {
        return ResponseEntity.ok(
                subscriberRepository.findAll()
        );
    }

    // Get subscriber
    @GetMapping("/{id}")
    public ResponseEntity<?> getSubscriber(
            @PathVariable Long id) {

        return subscriberRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.badRequest()
                                .body(Map.of(
                                        "message",
                                        "Subscriber not found"
                                ))
                );
    }

    // Delete subscriber
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubscriber(
            @PathVariable Long id) {

        try {

            if (!subscriberRepository.existsById(id)) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Subscriber not found"
                        ));
            }

            subscriberRepository.deleteById(id);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Subscriber deleted successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }
}