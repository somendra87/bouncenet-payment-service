package com.bouncenet.payments.exceptions;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.exceptions
 * @project bouncenet-payment-service
 * @since 03/04/26
 */
public class PaymentApplicationException extends RuntimeException{
  public PaymentApplicationException(String message){
    super(message);
  }

  public  PaymentApplicationException(String message, Throwable cause){
    super(message, cause);
  }
}
