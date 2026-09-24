package com.rishtabox.backend.service.admin;

import com.rishtabox.backend.repository.OrderRepository;
import com.rishtabox.backend.repository.ProductRepository;
import com.rishtabox.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AdminService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public AdminService(
            ProductRepository productRepository,
            UserRepository userRepository,
            OrderRepository orderRepository) {

        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public Map<String, Object> getDashboard() {

        Map<String, Object> dashboard =
                new LinkedHashMap<>();

        long totalProducts =
                productRepository.count();

        long totalUsers =
                userRepository.count();

        long totalOrders =
                orderRepository.count();

        long pendingOrders =
                orderRepository.countByOrderStatus(
                        "PAYMENT_PENDING"
                );

        long completedOrders =
                orderRepository.countByOrderStatus(
                        "DELIVERED"
                );

        Double totalSales =
                orderRepository.getTotalSales("PAID");

        if (totalSales == null) {
            totalSales = 0.0;
        }

        dashboard.put(
                "totalProducts",
                totalProducts
        );

        dashboard.put(
                "totalUsers",
                totalUsers
        );

        dashboard.put(
                "totalOrders",
                totalOrders
        );

        dashboard.put(
                "totalSales",
                totalSales
        );

        dashboard.put(
                "pendingOrders",
                pendingOrders
        );

        dashboard.put(
                "completedOrders",
                completedOrders
        );

        return dashboard;
    }
}