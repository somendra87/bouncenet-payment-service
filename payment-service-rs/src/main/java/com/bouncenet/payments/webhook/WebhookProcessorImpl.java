package com.bouncenet.payments.webhook;

import static com.bouncenet.payments.constants.BounceNetPaymentConstant.PAYMENT_CAPTURE_EVENT;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.controllers.webhook
 * @project bouncenet-payment-service
 * @since 29/04/26
 */
@Service
@RequiredArgsConstructor
public class WebhookProcessorImpl implements WebhookProcessor {

  private static final Logger log = LoggerFactory.getLogger(WebhookProcessorImpl.class);
  private final RazorPayConfigs rzpConfig;
  private final PaymentOrdersRepository paymentOrdersRepository;
  private final PaymentRepository paymentRepository;
  private final PaymentTransactionRepository paymentTransactionRepository;

  @Override
  @Transactional
  public void processWebhook(String payload, String signature) {
    // 1. verify webhook signature
    log.debug("Processing Razorpay Webhook");
    verifyWebhookSignature(payload, signature);

    JSONObject json = new JSONObject(payload);
    // 2. Event verification
    verifyPaymentCaptureWebhookEvent(json);
    log.debug("verifyPaymentCaptureWebhookEvent = {} ", json);

    // 3. Process the event
    JSONObject paymentEntity = json
            .getJSONObject("payload")
            .getJSONObject("payment")
            .getJSONObject("entity");

    String razorpayOrderId = paymentEntity.getString("order_id");
    String razorpayPaymentId = paymentEntity.getString("id");

    paymentOrdersRepository.findByRazorpayOrderId(razorpayOrderId).ifPresent(paymentOrder -> {
      updateOrderStausInPaymentOrders(paymentOrder);
      Payment payment = paymentRepository.findById(paymentOrder.getPaymentId())
              .orElseThrow(() -> new PaymentApplicationException("Payment not found"));
      updateStatusInPayment(payment);
      updatePaymentTransaction(signature, payment, razorpayPaymentId);
    });
  }

  private void updatePaymentTransaction(String signature, Payment payment, String razorpayPaymentId) {
    if (paymentTransactionRepository.findByRazorpayPaymentId(razorpayPaymentId).isPresent()) {
      return; // already processed, skip
    }
    PaymentTransaction txn = new PaymentTransaction();
    txn.setPaymentId(payment.getId());
    txn.setRazorpayPaymentId(razorpayPaymentId);
    txn.setRazorpaySignature(signature);
    txn.setPaymentStatus(Status.PAID.getName());
    txn.setAmount(payment.getAmount());
    paymentTransactionRepository.save(txn);
    log.debug("Payment transaction updated successfully = {} ", txn);
  }

  private void updateStatusInPayment(Payment payment) {
    payment.setStatus(Status.PAID.getName());
    paymentRepository.save(payment);
    log.debug("Payment status updated successfully = {} ", payment);
  }

  private void updateOrderStausInPaymentOrders(PaymentOrders rzpOrder) {
    rzpOrder.setOrderStatus(Status.PAID.getName());
    paymentOrdersRepository.save(rzpOrder);
    log.debug("Payment order updated successfully = {} ", rzpOrder);
  }

  private static void verifyPaymentCaptureWebhookEvent(JSONObject json) {
    if (!PAYMENT_CAPTURE_EVENT.getName().equalsIgnoreCase(json.getString("event"))) {
      return;
    }
  }

  private void verifyWebhookSignature(String payload, String signature) {
    try {
      log.debug("Verifying Razorpay Webhook Signature");
      log.debug("payload = {} , signarure = {}, webhooksecret = {}", payload, signature,
              rzpConfig.getWebHook().getWebhookSecret());
      boolean isValid = Utils.verifyWebhookSignature(payload, signature, rzpConfig.getWebHook().getWebhookSecret());
      if (!isValid) {
        throw new PaymentApplicationException("Invalid webhook signature");
      }
    } catch (Exception e) {
      throw new PaymentApplicationException("Webhook signature verification failed");
    }
  }
}
