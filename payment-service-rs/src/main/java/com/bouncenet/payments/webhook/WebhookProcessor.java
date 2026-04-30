package com.bouncenet.payments.webhook;


/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.controllers.webhook
 * @project bouncenet-payment-service
 * @since 29/04/26
 */
public interface WebhookProcessor {
  void processWebhook(String payload, String webhookSecret);
}
