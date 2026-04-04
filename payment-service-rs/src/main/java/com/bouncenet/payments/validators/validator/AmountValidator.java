package com.bouncenet.payments.validators.validator;

import com.bouncenet.payments.validators.annotations.ValidAmount;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.validators.annotations
 * @project bouncenet-payment-service
 * @since 03/04/26
 */
public class AmountValidator implements ConstraintValidator<ValidAmount, BigDecimal> {
  @Override
  public void initialize(ValidAmount constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(BigDecimal value, ConstraintValidatorContext constraintValidatorContext) {
    return value != null
            && value.scale() <= 2
            && value.compareTo(BigDecimal.ONE) > 0;
  }
}
