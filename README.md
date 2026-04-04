## 1. Payment Service Responsibilities
- Create payment
- Create Razorpay order
- Track payment state
- Handle webhook
- Store transactions

## 2. Define API contract 
### Request 
```java
package com.bouncenet.payment.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(
        UUID userId,
        UUID entityId,
        String entityType,
        BigDecimal amount,
        String currency
) {}
```

### Response
```java
package com.bouncenet.payment.dto;

import java.util.UUID;

public record CreatePaymentResponse(
        UUID paymentId,
        String razorpayOrderId,
        String status
) {}
```