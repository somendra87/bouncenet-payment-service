package com.bouncenet.payments.constants;


/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.constants
 * @project bouncenet-payment-service
 * @since 03/04/26
 */
public enum BounceNetPaymentConstant {
  PAYMENT_CAPTURED_EVENT("payment.captured"),
  PAYMENT_FAILED_EVENT("payment.failed");

  private final String name;
  BounceNetPaymentConstant(String name) {
    this.name = name;
  }

  public String getName(){
    return this.name;
  }
}
