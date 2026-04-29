package com.bouncenet.payments.controllers;

import com.bouncenet.payments.dao.entity.WebhookEvent;
import com.bouncenet.payments.dao.repository.WebhookEventRepository;
import com.bouncenet.payments.exceptions.PaymentApplicationException;
import com.bouncenet.payments.service.PaymentService;
import com.bouncenet.payments.webhook.WebhookProcessor;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.controllers.webhook
 * @project bouncenet-payment-service
 * @since 04/04/26
 */
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

  private static final Logger log = LoggerFactory.getLogger(WebhookController.class);
  private final WebhookEventRepository webhookEventRepository;
  private final WebhookProcessor webhookProcessor;

  @PostMapping("/razorpay")
  public ResponseEntity<String> handleRzpWebhook(
          @RequestBody String payload,
          @RequestHeader("X-Razorpay-Signature") String rzpSignature
  ) {

    log.debug("Received Razorpay Webhook Payload: {}", payload);
    try {
      log.debug("Processing Razorpay Webhook");
        webhookProcessor.processWebhook(payload, rzpSignature);
    } catch (PaymentApplicationException exception) {
      return ResponseEntity.badRequest().body(exception.getMessage());
    }

    log.debug("Saving Razorpay Webhook event to DB");
    JSONObject json = new JSONObject(payload);

    WebhookEvent event = new WebhookEvent();
    event.setEventId(json.optString("id", UUID.randomUUID().toString()));
    event.setEventType(json.optString("event", "unknown"));
    event.setPayload(payload);
    event.setProcessed(true);
    webhookEventRepository.save(event);
    log.debug("Webhook saved to DB");
    return ResponseEntity.ok("OK");
  }
}
