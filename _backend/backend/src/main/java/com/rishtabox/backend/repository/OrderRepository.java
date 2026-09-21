package com.rishtabox.backend.repository;

import com.rishtabox.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    // Check whether the user purchased a specific product
    @Query("""
            SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END
            FROM Order o
            JOIN o.items oi
            WHERE o.user.id = :userId
              AND oi.product.id = :productId
              AND o.orderStatus = :orderStatus
              AND o.paymentStatus = :paymentStatus
              AND o.orderStatus <> 'CANCELLED'
            """)
    boolean existsByUserIdAndItemsProductIdAndOrderStatusAndPaymentStatus(
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("orderStatus") String orderStatus,
            @Param("paymentStatus") String paymentStatus
    );
}