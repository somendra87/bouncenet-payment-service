package com.bouncenet.payments.dao.repository;

import com.bouncenet.payments.dao.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
  List<Payment> findByExternalUserId(UUID externalUserId);
  List<Payment> findByExternalEntityId(UUID externalEntityId);
  List<Payment> findByStatus(String status);
}