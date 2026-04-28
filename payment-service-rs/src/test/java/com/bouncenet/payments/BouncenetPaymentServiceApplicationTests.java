package com.bouncenet.payments;

import com.bouncenet.payments.dao.repository.PaymentOrdersRepository;
import com.bouncenet.payments.dao.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class BouncenetPaymentServiceApplicationTests {

	@MockitoBean
	private PaymentRepository paymentRepository;

	@MockitoBean
	private PaymentOrdersRepository paymentOrdersRepository;

	@MockitoBean
	private RazorpayClient razorpayClient;

	@Test
	void contextLoads() {
	}
}
