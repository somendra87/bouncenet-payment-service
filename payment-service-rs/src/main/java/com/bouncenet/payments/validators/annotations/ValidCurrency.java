package com.bouncenet.payments.validators.annotations;

import com.bouncenet.payments.validators.validator.CurrencyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.validators.annotations
 * @project bouncenet-payment-service
 * @since 03/04/26
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrencyValidator.class)
public @interface ValidCurrency {
  String message() default "Unsupported Currency";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
