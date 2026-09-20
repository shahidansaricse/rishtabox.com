package com.rishtabox.backend.controller;

import com.rishtabox.backend.service.SubscriberService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/subscribers")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(
            SubscriberService subscriberService) {

        this.subscriberService = subscriberService;
    }

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
                            "message", error.getMessage()
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
}