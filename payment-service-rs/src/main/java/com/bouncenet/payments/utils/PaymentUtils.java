package com.bouncenet.payments.utils;

import com.bouncenet.payments.dao.entity.Payment;
import com.bouncenet.payments.dao.entity.PaymentOrders;
import com.bouncenet.payments.dto.request.CreatePaymentRequestDto;
import com.bouncenet.payments.dto.response.CreatePaymentResponseDto;
import com.bouncenet.payments.enums.Status;
import com.razorpay.Order;

import java.util.UUID;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.utils
 * @project bouncenet-payment-service
 * @since 03/04/26
 */
public class PaymentUtils {
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
            .currency(reqDto.getCurrency()!=null ? reqDto.getCurrency() : "INR")
            .status(Status.CREATED.getName())
            .description(reqDto.getDescription())
            .build();
  }

  public static CreatePaymentResponseDto toPaymentResponse(Payment payment, Order rzpOrder) {
    if (null == payment || null == rzpOrder){
      return null;
    }
    return CreatePaymentResponseDto.builder()
            .paymentId(payment.getId())
            .razorpayOrderId(rzpOrder.get("id"))
            .status(payment.getStatus())
            .build();
  }

}
