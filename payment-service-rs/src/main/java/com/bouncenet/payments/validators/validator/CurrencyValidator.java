package com.bouncenet.payments.validators.validator;

import com.bouncenet.payments.enums.CurrencyType;
import com.bouncenet.payments.validators.annotations.ValidCurrency;
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
public class CurrencyValidator implements ConstraintValidator<ValidCurrency, String > {
  @Override
  public boolean isValid(String  value, ConstraintValidatorContext constraintValidatorContext) {
    return value != null && CurrencyType.toMap().containsKey(value);
  }
}
