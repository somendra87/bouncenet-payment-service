package com.bouncenet.payments.client;

import com.bouncenet.payments.configs.RazorPayConfigs;
import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.configs
 * @project bouncenet-payment-service
 * @since 03/04/26
 */
@Configuration
@EnableConfigurationProperties(RazorPayConfigs.class)
public class RazorPayClient {
  @Autowired
  private RazorPayConfigs razorPayConfigs;

  @Bean
  public RazorpayClient razorpayClient() throws Exception {
    return new RazorpayClient(razorPayConfigs.getRazorPayId(), razorPayConfigs.getRazorPaySecret());
  }
}
