package com.bouncenet.payments.dao.repository;

import com.bouncenet.payments.dao.entity.PaymentOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentOrdersRepository extends JpaRepository<PaymentOrders, UUID> {

    Optional<PaymentOrders> findByRazorpayOrderId(String razorpayOrderId);

    Optional<PaymentOrders> findByPaymentId(UUID paymentId);
}
