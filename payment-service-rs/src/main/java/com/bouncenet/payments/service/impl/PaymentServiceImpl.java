package com.bouncenet.payments.service.impl;

import static com.bouncenet.payments.utils.PaymentUtils.toDomain;
import static com.bouncenet.payments.utils.PaymentUtils.toPaymentResponse;

import com.bouncenet.payments.dao.entity.Payment;
import com.bouncenet.payments.dao.entity.PaymentOrders;
import com.bouncenet.payments.dao.repository.PaymentOrdersRepository;
import com.bouncenet.payments.dao.repository.PaymentRepository;
import com.bouncenet.payments.dto.request.CreatePaymentRequestDto;
import com.bouncenet.payments.dto.request.VerifyPaymentRequestDto;
import com.bouncenet.payments.dto.response.CreatePaymentResponseDto;
import com.bouncenet.payments.dto.response.PaymentResponseDto;
import com.bouncenet.payments.enums.Status;
import com.bouncenet.payments.exceptions.PaymentApplicationException;
import com.bouncenet.payments.service.PaymentService;
import com.bouncenet.payments.utils.PaymentUtils;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.service
 * @project bouncenet-payment-service
 * @since 03/04/26
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentOrdersRepository paymentOrdersRepository;
  private final RazorpayClient razorpayClient;

  @Override
  public CreatePaymentResponseDto createPayment(CreatePaymentRequestDto createPaymentRequestDto) {
    // Step 1 : save payment request to the database
    Payment payment = toDomain(createPaymentRequestDto);
    paymentRepository.save(payment);

    // step 2: Create Razor Pay Order
    try{
      JSONObject orderRequest = new JSONObject();
      orderRequest.put("amount", createPaymentRequestDto.getAmount().multiply(new BigDecimal(100)));
      orderRequest.put("currency", createPaymentRequestDto.getCurrency());
      orderRequest.put("receipt", payment.getId().toString());

      // step 3 : Created Razorpay order
      Order rzpOrder = razorpayClient.orders.create(orderRequest);

      // step 4: save payment order
      PaymentOrders paymentOrders = PaymentOrders.builder()
          .paymentId(payment.getId())
          .razorpayOrderId(rzpOrder.get("id"))
          .amount(payment.getAmount())
          .currency(payment.getCurrency())
          .orderStatus(Status.CREATED.getName())
          .build();
      paymentOrdersRepository.save(paymentOrders);


      // step 5 : create response

      return toPaymentResponse(payment, rzpOrder);

    }catch (Exception exception){
      payment.setStatus(Status.FAILED.getName());
      paymentRepository.save(payment);
      throw new PaymentApplicationException("Failed to create Razor Pay order");
    }
  }

  @Override
  public PaymentResponseDto verifyPayment(VerifyPaymentRequestDto paymentId) {
    return null;
  }

  @Override
  public PaymentResponseDto getPayment(UUID paymentId) {
    return null;
  }
}
