package com.bouncenet.payments.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class RefundRequestDto {

    private UUID paymentId;
    private BigDecimal amount;
}
