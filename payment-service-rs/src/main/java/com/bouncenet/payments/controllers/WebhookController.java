package com.bouncenet.payments.controllers;

import com.bouncenet.payments.dao.entity.WebhookEvent;
import com.bouncenet.payments.dao.repository.WebhookEventRepository;
import com.bouncenet.payments.exceptions.PaymentApplicationException;
import com.bouncenet.payments.service.PaymentService;
import com.bouncenet.payments.webhook.WebhookProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  private final WebhookEventRepository webhookEventRepository;
  private final WebhookProcessor webhookProcessor;

  @PostMapping("/razorpay")
  public ResponseEntity<String> handleRzpWebhook(
          @RequestBody String payload,
          @RequestHeader("X-Razorpay-Signature") String rzpSignature
  ) {

    try {
        webhookProcessor.processWebhook(payload, rzpSignature);
    } catch (PaymentApplicationException exception) {
      return ResponseEntity.badRequest().body(exception.getMessage());
    }

    WebhookEvent event = new WebhookEvent();
    event.setPayload(payload);
    event.setProcessed(false);
    webhookEventRepository.save(event);
    return ResponseEntity.ok("OK");
  }
}
