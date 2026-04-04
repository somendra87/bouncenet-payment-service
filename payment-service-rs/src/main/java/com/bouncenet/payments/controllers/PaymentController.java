package com.bouncenet.payments.controllers;

import static org.springframework.http.HttpStatus.CREATED;

import com.bouncenet.payments.dto.request.CreatePaymentRequestDto;
import com.bouncenet.payments.dto.request.VerifyPaymentRequestDto;
import com.bouncenet.payments.dto.response.BaseResponseDto;
import com.bouncenet.payments.dto.response.CreatePaymentResponseDto;
import com.bouncenet.payments.dto.response.PaymentResponseDto;
import com.bouncenet.payments.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.UUID;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.controllers
 * @project bouncenet-payment-service
 * @since 03/04/26
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
  private final PaymentService paymentService;

  @PostMapping
  public ResponseEntity<BaseResponseDto<CreatePaymentResponseDto>> createPayment(
         @Valid @RequestBody CreatePaymentRequestDto createPaymentRequestDto
  ) {
    CreatePaymentResponseDto createPaymentResponse = paymentService.createPayment(createPaymentRequestDto);
    return ResponseEntity.status(CREATED)
            .body(BaseResponseDto.success(createPaymentResponse, "Payment Created"));
  }

  @PostMapping("/verify")
  public ResponseEntity<BaseResponseDto<PaymentResponseDto>> verifyPayment(
          @RequestBody VerifyPaymentRequestDto verifyPaymentRequestDto
          ){
    PaymentResponseDto paymentResponseDto = paymentService.verifyPayment(verifyPaymentRequestDto);
    return ResponseEntity.ok().body(BaseResponseDto.success(paymentResponseDto, "Payment Verified"));
  }

  @GetMapping("/{paymentId}")
  public ResponseEntity<BaseResponseDto<PaymentResponseDto>> getPayment(
          @NotNull @PathVariable("paymentId") UUID paymentId){
    PaymentResponseDto paymentResponseDto = paymentService.getPayment(paymentId);
    return ResponseEntity.ok().body(BaseResponseDto.success(paymentResponseDto, "Payment fetched"));
  }
}
