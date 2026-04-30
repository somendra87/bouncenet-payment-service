package com.bouncenet.payments.webhook;

import static com.bouncenet.payments.constants.BounceNetPaymentConstant.PAYMENT_CAPTURED_EVENT;
import static com.bouncenet.payments.constants.BounceNetPaymentConstant.PAYMENT_FAILED_EVENT;

import com.bouncenet.payments.configs.RazorPayConfigs;
import com.bouncenet.payments.dao.entity.Payment;
import com.bouncenet.payments.dao.entity.PaymentOrders;
import com.bouncenet.payments.dao.entity.PaymentTransaction;
import com.bouncenet.payments.dao.repository.PaymentOrdersRepository;
import com.bouncenet.payments.dao.repository.PaymentRepository;
import com.bouncenet.payments.dao.repository.PaymentTransactionRepository;
import com.bouncenet.payments.enums.Status;
import com.bouncenet.payments.exceptions.PaymentApplicationException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.webhook
 * @project bouncenet-payment-service
 * @since 30/04/26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookProcessorHelper {
  private final RazorPayConfigs rzpConfig;
  private final PaymentOrdersRepository paymentOrdersRepository;
  private final PaymentRepository paymentRepository;
  private final PaymentTransactionRepository paymentTransactionRepository;

  public void processPaymentWebhookEvents(JSONObject json, String signature) {
    JSONObject paymentEntity = json
            .getJSONObject("payload")
            .getJSONObject("payment")
            .getJSONObject("entity");
    String eventType = json.getString("event");
    String razorpayOrderId = paymentEntity.getString("order_id");
    String razorpayPaymentId = paymentEntity.getString("id");

    if (PAYMENT_CAPTURED_EVENT.getName().equalsIgnoreCase(eventType)) {
      handlePaymentEvent(razorpayOrderId, razorpayPaymentId, signature, Status.PAID.getName());
    } else if (PAYMENT_FAILED_EVENT.getName().equalsIgnoreCase(eventType)) {
      handlePaymentEvent(razorpayOrderId, razorpayPaymentId, signature, Status.FAILED.getName());
    } else {
      log.warn("UNRECOGNIZED_PAYMENT_EVENT_OP | Unhandled webhook event = {} ", eventType);
    }

  }

  private void handlePaymentEvent(
          String razorpayOrderId,
          String razorpayPaymentId,
          String signature,
          String status
  ) {
    paymentOrdersRepository.findByRazorpayOrderId(razorpayOrderId).ifPresent(paymentOrder -> {
      if (Status.PAID.getName().equals(paymentOrder.getOrderStatus())) return;
      updateOrderStausInPaymentOrders(paymentOrder, status);
      Payment payment = paymentRepository.findById(paymentOrder.getPaymentId())
              .orElseThrow(() -> new PaymentApplicationException("Payment not found"));
      updateStatusInPayment(payment,status);
      updatePaymentTransaction(signature, payment, razorpayPaymentId, status);
    });
  }

  public void verifyWebhookSignature(String payload, String signature) {
    try {
      log.debug("Verifying Razorpay Webhook Signature");
      log.debug("payload = {} , signature = {}, webhookSecret = {}", payload, signature,
              rzpConfig.getWebHook().getWebhookSecret());
      boolean isValid = Utils.verifyWebhookSignature(payload, signature, rzpConfig.getWebHook().getWebhookSecret());
      if (!isValid) {
        throw new PaymentApplicationException("Invalid webhook signature");
      }
    } catch (Exception e) {
      throw new PaymentApplicationException("Webhook signature verification failed");
    }
  }

  private void updatePaymentTransaction(
          String signature,
          Payment payment,
          String razorpayPaymentId,
          String status
  ) {
    if (paymentTransactionRepository.findByRazorpayPaymentId(razorpayPaymentId).isPresent()) {
      return; // already processed, skip
    }
    PaymentTransaction txn = new PaymentTransaction();
    txn.setPaymentId(payment.getId());
    txn.setRazorpayPaymentId(razorpayPaymentId);
    txn.setRazorpaySignature(signature);
    txn.setPaymentStatus(status);
    txn.setAmount(payment.getAmount());
    paymentTransactionRepository.save(txn);
    log.debug("Payment transaction updated successfully = {} ", txn);
  }

  private void updateStatusInPayment(Payment payment, String status) {
    payment.setStatus(status);
    paymentRepository.save(payment);
    log.debug("Payment status updated successfully = {} ", payment);
  }

  private void updateOrderStausInPaymentOrders(PaymentOrders rzpOrder, String status) {
    rzpOrder.setOrderStatus(status);
    paymentOrdersRepository.save(rzpOrder);
    log.debug("Payment order updated successfully = {} ", rzpOrder);
  }
}
