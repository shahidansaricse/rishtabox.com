package com.rishtabox.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rishtabox.backend.config.ShiprocketConfig;
import com.rishtabox.backend.entity.Order;
import com.rishtabox.backend.entity.OrderItem;
import com.rishtabox.backend.entity.Product;
import com.rishtabox.backend.repository.OrderRepository;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShiprocketService {

    private final ShiprocketConfig shiprocketConfig;
    private final OrderRepository orderRepository;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ShiprocketService(
            ShiprocketConfig shiprocketConfig,
            OrderRepository orderRepository) {

        this.shiprocketConfig = shiprocketConfig;
        this.orderRepository = orderRepository;

        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }


    // =========================================================
    // LOGIN
    // =========================================================

    public String login() {

        String url =
                shiprocketConfig.getBaseUrl()
                        + "/v1/external/auth/login";

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        Map<String, String> loginRequest =
                new HashMap<>();

        loginRequest.put(
                "email",
                shiprocketConfig.getEmail()
        );

        loginRequest.put(
                "password",
                shiprocketConfig.getPassword()
        );

        HttpEntity<Map<String, String>> request =
                new HttpEntity<>(
                        loginRequest,
                        headers
                );

        try {

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            request,
                            String.class
                    );

            System.out.println(
                    "SHIPROCKET LOGIN RESPONSE: "
                            + response.getBody()
            );

            if (!response.getStatusCode()
                    .is2xxSuccessful()) {

                throw new RuntimeException(
                        "Shiprocket login failed: "
                                + response.getStatusCode()
                                + " "
                                + response.getBody()
                );
            }

            if (response.getBody() == null ||
                    response.getBody().isBlank()) {

                throw new RuntimeException(
                        "Shiprocket returned empty login response"
                );
            }

            JsonNode json =
                    objectMapper.readTree(
                            response.getBody()
                    );

            JsonNode tokenNode =
                    json.get("token");

            if (tokenNode == null ||
                    tokenNode.isNull() ||
                    tokenNode.asText().isBlank()) {

                throw new RuntimeException(
                        "Shiprocket token not received"
                );
            }

            return tokenNode.asText();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Shiprocket login failed: "
                            + e.getMessage(),
                    e
            );
        }
    }


    // =========================================================
    // CREATE SHIPROCKET ORDER
    // =========================================================

    public Order createShiprocketOrder(
            Long orderId) {

        if (orderId == null) {

            throw new RuntimeException(
                    "Order ID is required"
            );
        }

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found: "
                                                + orderId
                                ));


        // =====================================================
        // ALREADY CREATED
        // =====================================================

        if (order.getShiprocketOrderId() != null &&
                order.getShiprocketShipmentId() != null) {

            return order;
        }


        // =====================================================
        // ORDER NUMBER
        // =====================================================

        if (order.getOrderNumber() == null ||
                order.getOrderNumber().isBlank()) {

            throw new RuntimeException(
                    "Order number is missing"
            );
        }


        // =====================================================
        // SHIPPING VALIDATION
        // =====================================================

        if (order.getShippingAddress() == null ||
                order.getShippingAddress().isBlank()) {

            throw new RuntimeException(
                    "Shipping address is missing"
            );
        }

        if (order.getShippingCity() == null ||
                order.getShippingCity().isBlank()) {

            throw new RuntimeException(
                    "Shipping city is missing"
            );
        }

        if (order.getShippingState() == null ||
                order.getShippingState().isBlank()) {

            throw new RuntimeException(
                    "Shipping state is missing"
            );
        }

        if (order.getShippingPincode() == null ||
                !order.getShippingPincode()
                        .matches("\\d{6}")) {

            throw new RuntimeException(
                    "Invalid shipping pincode"
            );
        }


        // =====================================================
        // ITEMS VALIDATION
        // =====================================================

        if (order.getItems() == null ||
                order.getItems().isEmpty()) {

            throw new RuntimeException(
                    "Order has no items"
            );
        }


        // =====================================================
        // LOGIN
        // =====================================================

        String token = login();


        // =====================================================
        // REQUEST BODY
        // =====================================================

        Map<String, Object> requestBody =
                new HashMap<>();

        requestBody.put(
                "order_id",
                order.getOrderNumber()
        );

        requestBody.put(
                "order_date",
                order.getCreatedAt() != null
                        ? order.getCreatedAt().toString()
                        : LocalDateTime.now().toString()
        );

        requestBody.put(
                "pickup_location",
                "Home"
        );


        // =====================================================
        // CUSTOMER
        // =====================================================

        String customerName = "Customer";
        String customerEmail = "";
        String customerPhone = "";

        if (order.getUser() != null) {

            if (order.getUser().getName() != null &&
                    !order.getUser()
                            .getName()
                            .isBlank()) {

                customerName =
                        order.getUser()
                                .getName()
                                .trim();
            }

            if (order.getUser().getEmail() != null) {

                customerEmail =
                        order.getUser()
                                .getEmail()
                                .trim();
            }

            if (order.getUser().getPhone() != null) {

                customerPhone =
                        order.getUser()
                                .getPhone()
                                .trim();
            }
        }

        requestBody.put(
                "billing_customer_name",
                customerName
        );

        requestBody.put(
                "billing_last_name",
                ""
        );

        requestBody.put(
                "billing_address",
                order.getShippingAddress()
        );

        requestBody.put(
                "billing_address_2",
                ""
        );

        requestBody.put(
                "billing_city",
                order.getShippingCity()
        );

        requestBody.put(
                "billing_pincode",
                Integer.parseInt(
                        order.getShippingPincode()
                )
        );

        requestBody.put(
                "billing_state",
                order.getShippingState()
        );

        requestBody.put(
                "billing_country",
                "India"
        );

        requestBody.put(
                "billing_email",
                customerEmail
        );

        requestBody.put(
                "billing_phone",
                customerPhone
        );


        // =====================================================
        // SHIPPING
        // =====================================================

        requestBody.put(
                "shipping_is_billing",
                true
        );


        // =====================================================
        // PAYMENT
        // =====================================================

        if ("COD".equalsIgnoreCase(
                order.getPaymentMethod())) {

            requestBody.put(
                    "payment_method",
                    "COD"
            );

        } else {

            requestBody.put(
                    "payment_method",
                    "Prepaid"
            );
        }


        // =====================================================
        // TOTAL
        // =====================================================

        requestBody.put(
                "sub_total",
                order.getTotalAmount()
        );


        // =====================================================
        // ITEMS
        // =====================================================

        List<Map<String, Object>> items =
                new ArrayList<>();

        for (OrderItem orderItem :
                order.getItems()) {

            if (orderItem == null) {

                throw new RuntimeException(
                        "Invalid order item"
                );
            }

            Product product =
                    orderItem.getProduct();

            if (product == null) {

                throw new RuntimeException(
                        "Product not found for order item"
                );
            }

            if (orderItem.getQuantity() == null ||
                    orderItem.getQuantity() <= 0) {

                throw new RuntimeException(
                        "Invalid quantity for product: "
                                + product.getName()
                );
            }

            if (orderItem.getPrice() == null) {

                throw new RuntimeException(
                        "Price missing for product: "
                                + product.getName()
                );
            }

            Map<String, Object> item =
                    new HashMap<>();

            item.put(
                    "name",
                    product.getName()
            );

            item.put(
                    "sku",
                    "RB-" + product.getId()
            );

            item.put(
                    "units",
                    orderItem.getQuantity()
            );

            item.put(
                    "selling_price",
                    orderItem.getPrice()
            );

            item.put(
                    "discount",
                    0
            );

            item.put(
                    "tax",
                    0
            );

            item.put(
                    "hsn",
                    ""
            );

            items.add(item);
        }

        requestBody.put(
                "order_items",
                items
        );


        // =====================================================
        // PACKAGE
        // =====================================================

        requestBody.put(
                "length",
                10
        );

        requestBody.put(
                "breadth",
                10
        );

        requestBody.put(
                "height",
                10
        );

        requestBody.put(
                "weight",
                0.5
        );


        System.out.println(
                "SHIPROCKET CREATE REQUEST: "
                        + requestBody
        );


        // =====================================================
        // HEADERS
        // =====================================================

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        headers.setBearerAuth(
                token
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        requestBody,
                        headers
                );


        // =====================================================
        // API
        // =====================================================

        String url =
                shiprocketConfig.getBaseUrl()
                        + "/v1/external/orders/create/adhoc";

        try {

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            request,
                            String.class
                    );

            System.out.println(
                    "SHIPROCKET CREATE RESPONSE: "
                            + response.getBody()
            );

            if (!response.getStatusCode()
                    .is2xxSuccessful()) {

                throw new RuntimeException(
                        "Shiprocket order creation failed: "
                                + response.getStatusCode()
                                + " "
                                + response.getBody()
                );
            }

            if (response.getBody() == null ||
                    response.getBody().isBlank()) {

                throw new RuntimeException(
                        "Shiprocket returned empty response"
                );
            }

            JsonNode json =
                    objectMapper.readTree(
                            response.getBody()
                    );

            JsonNode orderIdNode =
                    json.get("order_id");

            JsonNode shipmentIdNode =
                    json.get("shipment_id");

            if (orderIdNode == null ||
                    orderIdNode.isNull() ||
                    orderIdNode.asText().isBlank()) {

                throw new RuntimeException(
                        "Shiprocket did not return order_id. "
                                + response.getBody()
                );
            }

            if (shipmentIdNode == null ||
                    shipmentIdNode.isNull() ||
                    shipmentIdNode.asText().isBlank()) {

                throw new RuntimeException(
                        "Shiprocket did not return shipment_id. "
                                + response.getBody()
                );
            }

            order.setShiprocketOrderId(
                    orderIdNode.asLong()
            );

            order.setShiprocketShipmentId(
                    shipmentIdNode.asLong()
            );

            order.setShippingMode(
                    "SHIPROCKET"
            );

            order.setShipmentStatus(
                    "ORDER_CREATED"
            );

            return orderRepository.save(order);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Shiprocket order creation failed: "
                            + e.getMessage(),
                    e
            );
        }
    }


    // =========================================================
    // ASSIGN COURIER / AWB
    // =========================================================

    public Order assignCourier(
            Long orderId,
            Long shipmentId) {

        if (orderId == null) {

            throw new RuntimeException(
                    "Order ID is required"
            );
        }

        if (shipmentId == null) {

            throw new RuntimeException(
                    "Shipment ID is required"
            );
        }

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                ));

        if (order.getShiprocketShipmentId() == null) {

            throw new RuntimeException(
                    "Shiprocket shipment ID not found"
            );
        }

        if (!order.getShiprocketShipmentId()
                .equals(shipmentId)) {

            throw new RuntimeException(
                    "Shipment ID does not belong to this order"
            );
        }


        // =====================================================
        // LOGIN
        // =====================================================

        String token = login();


        // =====================================================
        // AWB API
        // =====================================================

        String url =
                shiprocketConfig.getBaseUrl()
                        + "/v1/external/courier/assign/awb";


        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        headers.setBearerAuth(
                token
        );


        Map<String, Object> body =
                new HashMap<>();

        body.put(
                "shipment_id",
                shipmentId
        );


        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        body,
                        headers
                );


        try {

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            request,
                            String.class
                    );


            System.out.println(
                    "SHIPROCKET AWB RESPONSE: "
                            + response.getBody()
            );


            if (!response.getStatusCode()
                    .is2xxSuccessful()) {

                throw new RuntimeException(
                        "Courier assignment failed: "
                                + response.getStatusCode()
                                + " "
                                + response.getBody()
                );
            }


            if (response.getBody() == null ||
                    response.getBody().isBlank()) {

                throw new RuntimeException(
                        "Shiprocket returned empty AWB response"
                );
            }


            JsonNode json =
                    objectMapper.readTree(
                            response.getBody()
                    );


            // =================================================
            // FIND RESPONSE DATA
            // =================================================

            JsonNode responseNode =
                    json.get("response");

            JsonNode data = null;


            if (responseNode != null) {

                data =
                        responseNode.get("data");
            }


            if (data == null) {

                data =
                        json.get("data");
            }


            if (data == null ||
                    data.isNull()) {

                throw new RuntimeException(
                        "Shiprocket did not return courier data. "
                                + response.getBody()
                );
            }


            // =================================================
            // AWB
            // =================================================

            JsonNode awbNode =
                    data.get("awb_code");


            if (awbNode != null &&
                    !awbNode.isNull() &&
                    !awbNode.asText().isBlank()) {

                String awb =
                        awbNode.asText().trim();

                order.setAwbCode(
                        awb
                );

                order.setTrackingId(
                        awb
                );
            }


            // =================================================
            // COURIER
            // =================================================

            JsonNode courierNode =
                    data.get("courier_name");


            if (courierNode != null &&
                    !courierNode.isNull() &&
                    !courierNode.asText().isBlank()) {

                order.setCourierName(
                        courierNode.asText().trim()
                );
            }


            // =================================================
            // TRACKING URL
            // =================================================

            JsonNode trackingUrlNode =
                    data.get("tracking_url");


            if (trackingUrlNode == null) {

                trackingUrlNode =
                        data.get("track_url");
            }


            if (trackingUrlNode != null &&
                    !trackingUrlNode.isNull() &&
                    !trackingUrlNode.asText().isBlank()) {

                order.setTrackingUrl(
                        trackingUrlNode.asText().trim()
                );
            }


            // =================================================
            // AWB VALIDATION
            // =================================================

            if (order.getAwbCode() == null ||
                    order.getAwbCode().isBlank()) {

                throw new RuntimeException(
                        "Shiprocket did not return AWB code. "
                                + response.getBody()
                );
            }


            // =================================================
            // FALLBACK TRACKING URL
            // =================================================

            if (order.getTrackingUrl() == null ||
                    order.getTrackingUrl().isBlank()) {

                order.setTrackingUrl(
                        "https://shiprocket.co/tracking/"
                                + order.getAwbCode()
                );
            }


            // =================================================
            // STATUS
            // =================================================

            order.setShipmentStatus(
                    "AWB_ASSIGNED"
            );


            // =================================================
            // SAVE
            // =================================================

            Order savedOrder =
                    orderRepository.save(order);


            System.out.println(
                    "AWB SAVED: "
                            + savedOrder.getAwbCode()
            );

            System.out.println(
                    "TRACKING ID SAVED: "
                            + savedOrder.getTrackingId()
            );

            System.out.println(
                    "COURIER SAVED: "
                            + savedOrder.getCourierName()
            );

            System.out.println(
                    "TRACKING URL SAVED: "
                            + savedOrder.getTrackingUrl()
            );


            return savedOrder;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Courier assignment failed: "
                            + e.getMessage(),
                    e
            );
        }
    }


    // =========================================================
    // TRACK SHIPMENT
    // =========================================================

    public Order trackShipment(
            Long orderId) {

        if (orderId == null) {

            throw new RuntimeException(
                    "Order ID is required"
            );
        }


        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                ));


        String awb =
                order.getAwbCode();


        if (awb == null ||
                awb.isBlank()) {

            throw new RuntimeException(
                    "AWB code not available. "
                            + "Assign courier first."
            );
        }


        String token = login();


        String url =
                shiprocketConfig.getBaseUrl()
                        + "/v1/external/courier/track/awb/"
                        + awb;


        HttpHeaders headers =
                new HttpHeaders();

        headers.setBearerAuth(
                token
        );


        HttpEntity<Void> request =
                new HttpEntity<>(
                        headers
                );


        try {

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            request,
                            String.class
                    );


            System.out.println(
                    "SHIPROCKET TRACK RESPONSE: "
                            + response.getBody()
            );


            if (!response.getStatusCode()
                    .is2xxSuccessful()) {

                throw new RuntimeException(
                        "Shipment tracking failed: "
                                + response.getStatusCode()
                                + " "
                                + response.getBody()
                );
            }


            if (response.getBody() == null ||
                    response.getBody().isBlank()) {

                throw new RuntimeException(
                        "Shiprocket returned empty tracking response"
                );
            }


            JsonNode json =
                    objectMapper.readTree(
                            response.getBody()
                    );


            JsonNode trackingData =
                    json.get("tracking_data");


            if (trackingData == null ||
                    trackingData.isNull()) {

                throw new RuntimeException(
                        "Tracking data not found: "
                                + response.getBody()
                );
            }


            // =================================================
            // SHIPMENT STATUS
            // =================================================

            JsonNode statusNode =
                    trackingData.get(
                            "shipment_status"
                    );


            if (statusNode != null &&
                    !statusNode.isNull()) {

                order.setShipmentStatus(
                        statusNode.asText()
                );
            }


            // =================================================
            // TRACKING URL
            // =================================================

            JsonNode trackUrlNode =
                    trackingData.get(
                            "track_url"
                    );


            if (trackUrlNode != null &&
                    !trackUrlNode.isNull() &&
                    !trackUrlNode.asText().isBlank()) {

                order.setTrackingUrl(
                        trackUrlNode.asText()
                );
            }


            // =================================================
            // SHIPMENT TRACK
            // =================================================

            JsonNode shipmentTrack =
                    trackingData.get(
                            "shipment_track"
                    );


            if (shipmentTrack != null &&
                    shipmentTrack.isArray() &&
                    shipmentTrack.size() > 0) {

                JsonNode firstTrack =
                        shipmentTrack.get(0);


                // =============================================
                // AWB
                // =============================================

                JsonNode trackAwb =
                        firstTrack.get(
                                "awb_code"
                        );


                if (trackAwb != null &&
                        !trackAwb.isNull() &&
                        !trackAwb.asText().isBlank()) {

                    String awbCode =
                            trackAwb.asText().trim();

                    order.setAwbCode(
                            awbCode
                    );

                    order.setTrackingId(
                            awbCode
                    );
                }


                // =============================================
                // COURIER
                // =============================================

                JsonNode courier =
                        firstTrack.get(
                                "courier_name"
                        );


                if (courier != null &&
                        !courier.isNull() &&
                        !courier.asText().isBlank()) {

                    order.setCourierName(
                            courier.asText().trim()
                    );
                }
            }


            // =================================================
            // FALLBACK TRACKING ID
            // =================================================

            if (order.getTrackingId() == null ||
                    order.getTrackingId().isBlank()) {

                if (order.getAwbCode() != null &&
                        !order.getAwbCode().isBlank()) {

                    order.setTrackingId(
                            order.getAwbCode()
                    );
                }
            }


            // =================================================
            // FALLBACK TRACKING URL
            // =================================================

            if ((order.getTrackingUrl() == null ||
                    order.getTrackingUrl().isBlank()) &&
                    order.getAwbCode() != null &&
                    !order.getAwbCode().isBlank()) {

                order.setTrackingUrl(
                        "https://shiprocket.co/tracking/"
                                + order.getAwbCode()
                );
            }


            return orderRepository.save(order);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Shipment tracking failed: "
                            + e.getMessage(),
                    e
            );
        }
    }


    // =========================================================
    // GET SHIPPING INFORMATION BY ORDER ID
    // =========================================================

    public Order getShippingInformation(
            Long orderId) {

        if (orderId == null) {

            throw new RuntimeException(
                    "Order ID is required"
            );
        }

        return orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found: "
                                        + orderId
                        ));
    }


    // =========================================================
    // GET SHIPPING INFORMATION BY ORDER NUMBER
    // =========================================================

    public Order getShippingInformationByOrderNumber(
            String orderNumber) {

        if (orderNumber == null ||
                orderNumber.isBlank()) {

            throw new RuntimeException(
                    "Order number is required"
            );
        }

        return orderRepository
                .findByOrderNumber(orderNumber)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found: "
                                        + orderNumber
                        ));
    }
}