/*
 * Copyright 2026 BounceNet. All rights reserved.
 */

package com.bouncenet.payments.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.dao.entity
 * @project bouncenet-payment-service
 * @since 03/04/26
 */
@Entity
@Table(name = "payment_orders")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrders {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "payment_id", nullable = false)
  private UUID paymentId;

  @Column(name = "razorpay_order_id", nullable = false, unique = true, length = 250)
  private String razorpayOrderId;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 10)
  private String currency;

  @Column(name = "order_status", nullable = false, length = 50)
  private String orderStatus;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}
