package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.Subscriber;
import com.rishtabox.backend.service.SubscriberService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscribers")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(
            SubscriberService subscriberService) {

        this.subscriberService = subscriberService;
    }


    // =========================================================
    // CUSTOMER - SUBSCRIBE
    // POST /api/subscribers
    // =========================================================

    @PostMapping
    public ResponseEntity<?> subscribe(
            @RequestBody Map<String, String> request) {

        try {

            String email = request.get("email");

            String message =
                    subscriberService.subscribe(email);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", message
                    )
            );

        } catch (IllegalArgumentException error) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message",
                            error.getMessage()
                    )
            );

        } catch (Exception error) {

            error.printStackTrace();

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body(
                    Map.of(
                            "success", false,
                            "message",
                            "Unable to subscribe right now."
                    )
            );
        }
    }


    // =========================================================
    // ADMIN - GET ALL SUBSCRIBERS
    // GET /api/subscribers
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Subscriber>>
    getAllSubscribers() {

        try {

            List<Subscriber> subscribers =
                    subscriberService.getAllSubscribers();

            return ResponseEntity.ok(subscribers);

        } catch (Exception error) {

            error.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of());
        }
    }


    // =========================================================
    // ADMIN - GET SUBSCRIBER COUNT
    // GET /api/subscribers/count
    // =========================================================

    @GetMapping("/count")
    public ResponseEntity<?> getSubscriberCount() {

        try {

            long count =
                    subscriberService.getSubscriberCount();

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "count", count
                    )
            );

        } catch (Exception error) {

            error.printStackTrace();

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body(
                    Map.of(
                            "success", false,
                            "message",
                            "Unable to get subscriber count."
                    )
            );
        }
    }


    // =========================================================
    // ADMIN - DELETE SUBSCRIBER
    // DELETE /api/subscribers/{id}
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubscriber(
            @PathVariable Long id) {

        try {

            subscriberService.deleteSubscriber(id);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message",
                            "Subscriber deleted successfully."
                    )
            );

        } catch (IllegalArgumentException error) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message",
                            error.getMessage()
                    )
            );

        } catch (Exception error) {

            error.printStackTrace();

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body(
                    Map.of(
                            "success", false,
                            "message",
                            "Unable to delete subscriber."
                    )
            );
        }
    }
}