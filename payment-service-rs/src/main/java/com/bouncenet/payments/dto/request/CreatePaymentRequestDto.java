package com.bouncenet.payments.dto.request;

import com.bouncenet.payments.validators.annotations.ValidAmount;
import com.bouncenet.payments.validators.annotations.ValidCurrency;
import com.bouncenet.payments.validators.annotations.ValidEntityType;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CreatePaymentRequestDto {

    private UUID userId;

    private UUID entityId;

    @ValidEntityType
    private String entityType;

    @ValidAmount
    private BigDecimal amount;

    @ValidCurrency
    private String currency;

    private String description;
}
