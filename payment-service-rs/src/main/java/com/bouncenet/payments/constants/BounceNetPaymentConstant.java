package com.bouncenet.payments.constants;


/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.constants
 * @project bouncenet-payment-service
 * @since 03/04/26
 */
public enum BounceNetPaymentConstant {
  PAYMENT_CAPTURE_EVENT("payment.capture");

  private final String name;
  BounceNetPaymentConstant(String name) {
    this.name = name;
  }

  public String getName(){
    return this.name;
  }
}
