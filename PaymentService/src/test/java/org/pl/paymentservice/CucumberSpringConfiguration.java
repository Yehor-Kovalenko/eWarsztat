package org.pl.paymentservice;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = PaymentServiceApplication.class)
public class CucumberSpringConfiguration {
}
