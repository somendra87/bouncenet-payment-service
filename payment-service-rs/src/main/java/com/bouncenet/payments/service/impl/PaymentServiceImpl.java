package com.bouncenet.payments.service.impl;

import static com.bouncenet.payments.utils.PaymentUtils.createRzpOrderRequest;
import static com.bouncenet.payments.utils.PaymentUtils.savePaymentOrders;
import static com.bouncenet.payments.utils.PaymentUtils.toDomain;
import static com.bouncenet.payments.utils.PaymentUtils.toPaymentResponse;
import static com.bouncenet.payments.utils.PaymentUtils.toPaymentResponseDto;
import static com.bouncenet.payments.utils.PaymentUtils.toPaymentTransaction;

import com.bouncenet.payments.configs.RazorPayConfigs;
import com.bouncenet.payments.dao.entity.Payment;
import com.bouncenet.payments.dao.entity.PaymentOrders;
import com.bouncenet.payments.dao.entity.PaymentTransaction;
import com.bouncenet.payments.dao.repository.PaymentOrdersRepository;
import com.bouncenet.payments.dao.repository.PaymentRepository;
import com.bouncenet.payments.dao.repository.PaymentTransactionRepository;
import com.bouncenet.payments.dto.request.CreatePaymentRequestDto;
import com.bouncenet.payments.dto.request.VerifyPaymentRequestDto;
import com.bouncenet.payments.dto.response.CreatePaymentResponseDto;
import com.bouncenet.payments.dto.response.PaymentResponseDto;
import com.bouncenet.payments.enums.Status;
import com.bouncenet.payments.exceptions.PaymentApplicationException;
import com.bouncenet.payments.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
  private final PaymentRepository paymentRepository;
  private final PaymentOrdersRepository paymentOrdersRepository;
  private final PaymentTransactionRepository paymentTransactionRepository;
  private final RazorpayClient razorpayClient;
  private final RazorPayConfigs razorPayConfigs;

  @Override
  public CreatePaymentResponseDto createPayment(CreatePaymentRequestDto createPaymentRequestDto) {
    // Step 1 : save payment request to the database
    Payment payment = toDomain(createPaymentRequestDto);
    paymentRepository.save(payment);
    log.info("CREATE_PAYMENT_OP | To Domain | persisted payment = {} ", payment);

    // step 2: Create Razor Pay Order
    try{
      JSONObject orderRequest = createRzpOrderRequest(createPaymentRequestDto, payment);
      log.info("CREATE_PAYMENT_OP | Razor Pay Order Request = {} ", orderRequest);

      // step 3 : Created Razorpay order
      Order rzpOrder = razorpayClient.orders.create(orderRequest);
      log.info("CREATE_PAYMENT_OP | Razor Pay Order created = {} ", rzpOrder);

      // step 4: save payment order
      PaymentOrders paymentOrders = savePaymentOrders(payment, rzpOrder);
      paymentOrdersRepository.save(paymentOrders);
      log.info("CREATE_PAYMENT_OP | Payment Orders persisted = {} ", paymentOrders);

      // step 5 : create response
      CreatePaymentResponseDto paymentResponse = toPaymentResponse(payment, rzpOrder);
      log.info("CREATE_PAYMENT_OP | Create payment response = {} ", paymentResponse);
      return  paymentResponse;
    }catch (Exception exception){
      payment.setStatus(Status.FAILED.getName());
      paymentRepository.save(payment);
      throw new PaymentApplicationException("Failed to create Razor Pay order");
    }
  }


  @Override
  @Transactional
  public PaymentResponseDto verifyPayment(VerifyPaymentRequestDto request) {
    // Step 1: Find payment order by razorpay order id
    PaymentOrders paymentOrder = paymentOrdersRepository
            .findByRazorpayOrderId(request.getRazorpayOrderId())
            .orElseThrow(() -> new PaymentApplicationException("Payment order not found"));

    log.info("VERIFY_PAYMENT_OP | Verify Payment Order {}", paymentOrder);

    Payment payment = paymentRepository.findById(paymentOrder.getPaymentId())
            .orElseThrow(() -> new PaymentApplicationException("Payment not found"));

    log.info("VERIFY_PAYMENT_OP | Verify Payment {}", payment);

    // Step 2: Verify Razorpay signature
    try {
      JSONObject attributes = new JSONObject();
      attributes.put("razorpay_order_id", request.getRazorpayOrderId());
      attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
      attributes.put("razorpay_signature", request.getRazorpaySignature());

      boolean isValid = Utils.verifyPaymentSignature(attributes, razorPayConfigs.getRazorPaySecret());
      if (!isValid) {
        throw new PaymentApplicationException("Invalid payment signature");
      }
    } catch (PaymentApplicationException e) {
      throw e;
    } catch (Exception e) {
      throw new PaymentApplicationException("Signature verification failed");
    }

    // Step 3: Update order status
    paymentOrder.setOrderStatus(Status.PAID.getName());
    paymentOrdersRepository.save(paymentOrder);
    log.info("VERIFY_PAYMENT_OP | Updated Payment Order {}", paymentOrder);

    // Step 4: Save payment transaction
    PaymentTransaction txn = toPaymentTransaction(payment, request, Status.PAID.getName());
    paymentTransactionRepository.save(txn);
    log.info("VERIFY_PAYMENT_OP | Updated Payment Transaction {}", txn);

    // Step 5: Update payment status
    payment.setStatus(Status.PAID.getName());
    paymentRepository.save(payment);
    log.info("VERIFY_PAYMENT_OP | Updated Payment {}", payment);


    return toPaymentResponseDto(payment);
  }

  @Override
  public PaymentResponseDto getPayment(UUID paymentId) {

    Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentApplicationException("Payment not found"));

    return toPaymentResponseDto(payment);
  }
}
