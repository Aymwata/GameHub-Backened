package com.gamehub.shippingservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shippings")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Shipping extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String carrier;

    @Column(unique = true)
    private String trackingNumber;

    @Column(nullable = false)
    private String status;

    private LocalDateTime shippingDate;
    private LocalDateTime deliveryDate;
}