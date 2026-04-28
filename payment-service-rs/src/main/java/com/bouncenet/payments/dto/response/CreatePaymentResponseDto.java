package com.bouncenet.payments.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentResponseDto {

    private UUID paymentId;
    private String razorpayOrderId;
    private String status;
    private BigDecimal amount;
    private String currency;
}
