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

  private final RazorPayConfigs rzpConfig;
  private final PaymentOrdersRepository paymentOrdersRepository;
  private final PaymentRepository paymentRepository;
  private final PaymentTransactionRepository paymentTransactionRepository;

  @Override
  @Transactional
  public void processWebhook(String payload, String signature) {
    // 1. verify webhook signature
    verifyWebhookSignature(payload, signature);

    JSONObject json = new JSONObject(payload);
    // 2. Event verification
    verifyPaymentCaptureWebhookEvent(json);

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
    PaymentTransaction txn = new PaymentTransaction();
    txn.setPaymentId(payment.getId());
    txn.setRazorpayPaymentId(razorpayPaymentId);
    txn.setRazorpaySignature(signature);
    txn.setPaymentStatus(Status.PAID.getName());
    txn.setAmount(payment.getAmount());
    paymentTransactionRepository.save(txn);
  }

  private void updateStatusInPayment(Payment payment) {
    payment.setStatus(Status.PAID.getName());
    paymentRepository.save(payment);
  }

  private void updateOrderStausInPaymentOrders(PaymentOrders rzpOrder) {
    rzpOrder.setOrderStatus(Status.PAID.getName());
    paymentOrdersRepository.save(rzpOrder);
  }

  private static void verifyPaymentCaptureWebhookEvent(JSONObject json) {
    if (!PAYMENT_CAPTURE_EVENT.getName().equalsIgnoreCase(json.getString("event"))) {
      return;
    }
  }

  private void verifyWebhookSignature(String payload, String signature) {
    try {
      boolean isValid = Utils.verifyWebhookSignature(payload, signature, rzpConfig.getWebHook().getWebhookSecret());
      if (!isValid) {
        throw new PaymentApplicationException("Invalid webhook signature");
      }
    } catch (Exception e) {
      throw new PaymentApplicationException("Webhook signature verification failed");
    }
  }
}
