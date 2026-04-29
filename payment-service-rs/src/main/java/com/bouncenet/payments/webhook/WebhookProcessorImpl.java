package com.bouncenet.payments.webhook;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.controllers.webhook
 * @project bouncenet-payment-service
 * @since 29/04/26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookProcessorImpl implements WebhookProcessor {

  private final WebhookProcessorHelper webhookProcessorHelper;

  @Override
  @Transactional
  public void processWebhook(String payload, String signature) {
    // 1. verify webhook signature
    log.debug("Processing Razorpay Webhook");
    webhookProcessorHelper.verifyWebhookSignature(payload, signature);

    JSONObject json = new JSONObject(payload);

    // 2. Event verification
    webhookProcessorHelper.processPaymentWebhookEvents(json, signature);
    log.debug("verifyPaymentCaptureWebhookEvent = {} ", json);

  }


}
