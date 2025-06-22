package org.pl.paymentservice.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.pl.paymentservice.InvoiceDTO;
import org.pl.paymentservice.TestContext;
import org.pl.paymentservice.entity.Client;
import org.pl.paymentservice.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentServiceSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestContext testContext;

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Given("the payment service is running")
    public void thePaymentServiceIsRunning() {
        // Handled by @SpringBootTest; no action needed
    }

    @Given("there are payments in the system")
    public void thereArePaymentsInTheSystem() {
        createPaymentInSystem();
    }

    @Given("there is a payment in the system")
    public void thereIsAPaymentInTheSystem() {
        createPaymentInSystem();
    }

    @Given("there is a payment with associated clients in the system")
    public void thereIsAPaymentWithAssociatedClientsInTheSystem() {
        createPaymentInSystem();
    }

    @Given("I have a valid payment object with clients")
    public void iHaveAValidPaymentObjectWithClients() {
        Client client1 = new Client();
        client1.setName("Client 1");
        client1.setClientType("Individual");

        Client client2 = new Client();
        client2.setName("Client 2");
        client2.setClientType("Business");

        Payment payment = new Payment();
        payment.setAmount(BigDecimal.valueOf(200.00));
        payment.setCurrency("EUR");
        payment.setDate(LocalDateTime.now());
        payment.setClient(client1);

        testContext.setPayment(payment);
    }

    @When("I send a GET request to {string}")
    public void iSendAGETRequestTo(String endpoint) {
        Response response = given()
                .contentType(ContentType.JSON)
                .get(endpoint);
        testContext.setResponse(response);
    }

    @When("I send a GET request to {string} with the payment ID")
    public void iSendAGETRequestToWithThePaymentId(String endpoint) {
        Long id = testContext.getPaymentId();
        Response response = given()
                .contentType(ContentType.JSON)
                .get(endpoint.replace("{id}", id.toString()));
        testContext.setResponse(response);
    }

    @When("I send a POST request to {string}")
    public void iSendAPOSTRequestTo(String endpoint) {
        Payment payment = testContext.getPayment();
        Response response = given()
                .contentType(ContentType.JSON)
                .body(payment)
                .post(endpoint);
        testContext.setResponse(response);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        Response response = testContext.getResponse();
        assertThat(response.getStatusCode(), is(statusCode));
    }

    @Then("the response should contain a list of payments")
    public void theResponseShouldContainAListOfPayments() {
        Response response = testContext.getResponse();
        Payment[] payments = response.as(Payment[].class);
        assertThat(payments, is(not(emptyArray())));
    }

    @Then("the response should contain the payment details")
    public void theResponseShouldContainThePaymentDetails() {
        Response response = testContext.getResponse();
        Payment payment = response.as(Payment.class);
        assertThat(payment.getId(), is(testContext.getPaymentId()));
        assertThat(payment.getAmount(), is(notNullValue()));
        assertThat(payment.getCurrency(), is(notNullValue()));
    }

    @Then("the response should contain the created payment details")
    public void theResponseShouldContainTheCreatedPaymentDetails() {
        Response response = testContext.getResponse();
        Payment payment = response.as(Payment.class);
        assertThat(payment.getId(), is(notNullValue()));
        assertThat(payment.getAmount(), is(testContext.getPayment().getAmount()));
        assertThat(payment.getCurrency(), is(testContext.getPayment().getCurrency()));
//        assertThat(payment.getClients().size(), is(2));
        testContext.setPaymentId(payment.getId()); // Store ID for potential later use
    }

    @Then("the response should contain an invoice with correct payment and client information")
    public void theResponseShouldContainAnInvoiceWithCorrectPaymentAndClientInformation() {
        Response response = testContext.getResponse();
        InvoiceDTO invoice = response.as(InvoiceDTO.class);
        Payment originalPayment = testContext.getPayment();
        BigDecimal expected = originalPayment.getAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual = invoice.getAmount().setScale(2, RoundingMode.HALF_UP);

        assertThat(invoice.getPaymentId(), is(testContext.getPaymentId()));
        assertThat(expected, is(actual));
        assertThat(invoice.getCurrency(), is(originalPayment.getCurrency()));
        assertThat(invoice.getClients().size(), is(1));
        assertThat(invoice.getClients().get(0).name(), is("Client 1"));
    }

    private void createPaymentInSystem() {
        iHaveAValidPaymentObjectWithClients();
        Response response = given()
                .contentType(ContentType.JSON)
                .body(testContext.getPayment())
                .post("/api/payments/admin/create");
        Payment createdPayment = response.as(Payment.class);
        testContext.setPayment(createdPayment);
        testContext.setPaymentId(createdPayment.getId());
    }
}