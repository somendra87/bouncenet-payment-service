package com.bouncenet.payments.configs;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.configs
 * @project bouncenet-payment-service
 * @since 03/04/26
 */
@Component
@Data
@ConfigurationProperties(prefix = "razorpay")
public class RazorPayConfigs {
  private String razorPayId;
  private String razorPaySecret;
}
