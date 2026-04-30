package com.bouncenet.payments.utils;

import com.bouncenet.payments.dao.entity.Payment;
import com.bouncenet.payments.dao.entity.PaymentOrders;
import com.bouncenet.payments.dao.entity.PaymentTransaction;
import com.bouncenet.payments.dto.request.CreatePaymentRequestDto;
import com.bouncenet.payments.dto.request.VerifyPaymentRequestDto;
import com.bouncenet.payments.dto.response.CreatePaymentResponseDto;
import com.bouncenet.payments.dto.response.PaymentResponseDto;
import com.bouncenet.payments.enums.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.Order;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.utils
 * @project bouncenet-payment-service
 * @since 03/04/26
 */
@Slf4j
public class PaymentUtils {
  private static ObjectMapper mapper = new ObjectMapper();
  private PaymentUtils() {
    // private constructor
  }

  public static Payment toDomain(CreatePaymentRequestDto reqDto) {
    if (reqDto == null) {
      return null;
    }
    return Payment.builder()
            .externalUserId(UUID.randomUUID())
            .externalEntityId(UUID.randomUUID())
            .externalEntityType(reqDto.getEntityType())
            .amount(reqDto.getAmount())
            .currency(reqDto.getCurrency() != null ? reqDto.getCurrency() : "INR")
            .status(Status.CREATED.getName())
            .description(reqDto.getDescription())
            .build();
  }

  public static CreatePaymentResponseDto toPaymentResponse(Payment payment, Order rzpOrder) {
    if (null == payment || null == rzpOrder) {
      return null;
    }
    return CreatePaymentResponseDto.builder()
            .paymentId(payment.getId())
            .razorpayOrderId(rzpOrder.get("id"))
            .status(payment.getStatus())
            .amount(payment.getAmount())
            .currency(payment.getCurrency())
            .build();
  }

  public static PaymentOrders savePaymentOrders(Payment payment, Order rzpOrder) {
    if (null == payment || null == rzpOrder) {
      return null;
    }
    return PaymentOrders.builder()
            .paymentId(payment.getId())
            .razorpayOrderId(rzpOrder.get("id"))
            .amount(payment.getAmount())
            .currency(payment.getCurrency())
            .orderStatus(Status.CREATED.getName())
            .build();
  }

  public static @NonNull JSONObject createRzpOrderRequest(
          CreatePaymentRequestDto createPaymentRequestDto,
          Payment payment) {
    JSONObject orderRequest = new JSONObject();
    orderRequest.put("amount", createPaymentRequestDto.getAmount().multiply(new BigDecimal(100)));
    orderRequest.put("currency", createPaymentRequestDto.getCurrency());
    orderRequest.put("receipt", payment.getId().toString());
    return orderRequest;
  }

  public static PaymentTransaction toPaymentTransaction(
          Payment payment, VerifyPaymentRequestDto request, String status) {
    if (null == payment || null == request) {
      return null;
    }
    PaymentTransaction txn = new PaymentTransaction();
    txn.setPaymentId(payment.getId());
    txn.setRazorpayPaymentId(request.getRazorpayPaymentId());
    txn.setRazorpaySignature(request.getRazorpaySignature());
    txn.setPaymentStatus(status);
    txn.setAmount(payment.getAmount());
    try {
      log.info("Payment Transaction : {}", mapper.writeValueAsString(txn));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return txn;
  }

  public static PaymentResponseDto toPaymentResponseDto(Payment payment) {
    if (null == payment) {
      return null;
    }
    PaymentResponseDto dto = new PaymentResponseDto();
    dto.setId(payment.getId());
    dto.setExternalUserId(payment.getExternalUserId());
    dto.setExternalEntityId(payment.getExternalEntityId());
    dto.setExternalEntityType(payment.getExternalEntityType());
    dto.setAmount(payment.getAmount());
    dto.setCurrency(payment.getCurrency());
    dto.setStatus(payment.getStatus());
    dto.setDescription(payment.getDescription());
    dto.setCreatedAt(payment.getCreatedAt());
    dto.setUpdatedAt(payment.getUpdatedAt());
    log.info("Payment Response Dto = {}", dto);
    return dto;
  }

}
