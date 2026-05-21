package com.gamehub.shippingservice.models.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShippingResponseDTO {
    private Long id;
    private Long orderId;
    private Long userId;
    private String address;
    private String carrier;
    private String trackingNumber;
    private String status;
    private LocalDateTime shippingDate;
    private LocalDateTime deliveryDate;
}
