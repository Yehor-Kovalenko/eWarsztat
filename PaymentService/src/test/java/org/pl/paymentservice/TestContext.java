package org.pl.paymentservice;

import io.restassured.response.Response;
import org.pl.paymentservice.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class TestContext {
    private Payment payment;
    private Response response;
    private Long paymentId;

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    public Response getResponse() { return response; }
    public void setResponse(Response response) { this.response = response; }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
}