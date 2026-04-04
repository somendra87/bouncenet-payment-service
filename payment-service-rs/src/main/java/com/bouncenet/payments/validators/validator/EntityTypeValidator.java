package com.bouncenet.payments.validators.validator;

import com.bouncenet.payments.enums.EntityType;
import com.bouncenet.payments.validators.annotations.ValidEntityType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.validators.annotations
 * @project bouncenet-payment-service
 * @since 03/04/26
 */
public class EntityTypeValidator implements ConstraintValidator<ValidEntityType, String> {

  @Override
  public boolean isValid(String entityType, ConstraintValidatorContext constraintValidatorContext) {
    return entityType != null && EntityType.toMap().containsKey(entityType);
  }
}
