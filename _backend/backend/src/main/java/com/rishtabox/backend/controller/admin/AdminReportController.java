package com.rishtabox.backend.controller.admin;

import com.rishtabox.backend.repository.OrderRepository;
import com.rishtabox.backend.repository.ProductRepository;
import com.rishtabox.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public AdminReportController(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // Overall report
    @GetMapping
    public ResponseEntity<Map<String, Object>> getReport() {

        Map<String, Object> report =
                new LinkedHashMap<>();

        Double totalSales =
                orderRepository.getTotalSales("PAID");

        if (totalSales == null) {
            totalSales = 0.0;
        }

        report.put(
                "totalProducts",
                productRepository.count()
        );

        report.put(
                "totalUsers",
                userRepository.count()
        );

        report.put(
                "totalOrders",
                orderRepository.count()
        );

        report.put(
                "totalSales",
                totalSales
        );

        report.put(
                "pendingOrders",
                orderRepository.countByOrderStatus(
                        "PAYMENT_PENDING"
                )
        );

        report.put(
                "placedOrders",
                orderRepository.countByOrderStatus(
                        "PLACED"
                )
        );

        report.put(
                "processingOrders",
                orderRepository.countByOrderStatus(
                        "PROCESSING"
                )
        );

        report.put(
                "shippedOrders",
                orderRepository.countByOrderStatus(
                        "SHIPPED"
                )
        );

        report.put(
                "deliveredOrders",
                orderRepository.countByOrderStatus(
                        "DELIVERED"
                )
        );

        report.put(
                "cancelledOrders",
                orderRepository.countByOrderStatus(
                        "CANCELLED"
                )
        );

        report.put(
                "paidOrders",
                orderRepository.countByPaymentStatus(
                        "PAID"
                )
        );

        report.put(
                "pendingPayments",
                orderRepository.countByPaymentStatus(
                        "PENDING"
                )
        );

        report.put(
                "failedPayments",
                orderRepository.countByPaymentStatus(
                        "FAILED"
                )
        );

        return ResponseEntity.ok(report);
    }

    // Sales only
    @GetMapping("/sales")
    public ResponseEntity<Map<String, Object>> getSales() {

        Double totalSales =
                orderRepository.getTotalSales("PAID");

        if (totalSales == null) {
            totalSales = 0.0;
        }

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put("totalSales", totalSales);
        result.put(
                "paidOrders",
                orderRepository.countByPaymentStatus("PAID")
        );

        return ResponseEntity.ok(result);
    }
}