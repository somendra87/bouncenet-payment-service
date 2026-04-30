package com.bouncenet.payments.service;


import com.bouncenet.payments.dto.request.CreatePaymentRequestDto;
import com.bouncenet.payments.dto.request.VerifyPaymentRequestDto;
import com.bouncenet.payments.dto.response.CreatePaymentResponseDto;
import com.bouncenet.payments.dto.response.PaymentResponseDto;

import java.util.UUID;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.service
 * @project bouncenet-payment-service
 * @since 03/04/26
 */
public interface PaymentService {
  CreatePaymentResponseDto createPayment(CreatePaymentRequestDto createPaymentRequestDto);
  PaymentResponseDto verifyPayment(VerifyPaymentRequestDto paymentId);
  PaymentResponseDto getPayment(UUID paymentId);
}
