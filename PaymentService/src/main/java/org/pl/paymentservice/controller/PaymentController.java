package org.pl.paymentservice.controller;

import org.pl.paymentservice.InvoiceDTO;
import org.pl.paymentservice.entity.Client;
import org.pl.paymentservice.entity.Payment;
import org.pl.paymentservice.service.ExternalExchangeRatesService;
import org.pl.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ExternalExchangeRatesService externalExchangeRatesService;

    @GetMapping("/")
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/admin/create")
    public Payment createPayment(@RequestBody Payment payment) {
        // TODO validation etc.
        return paymentService.processPayment(payment);
    }


    @GetMapping("/invoice/{paymentId}")
    public ResponseEntity<?> generateInvoice(@PathVariable Long paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setPaymentId(payment.getId());
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setPaymentDate(payment.getDate());
        invoice.setAmount(payment.getAmount());
        invoice.setCurrency(payment.getCurrency());

        Client client = payment.getClient();
        List<InvoiceDTO.ClientInfo> clientInfos = client != null
                ? List.of(new InvoiceDTO.ClientInfo(client.getId(), client.getName(), client.getClientType()))
                : Collections.emptyList();

        invoice.setClients(clientInfos);

        return ResponseEntity.ok(invoice);
    }
    @GetMapping("/exchange/rates")
    public ResponseEntity<?> getExchangeRates() {
        return ResponseEntity.ok(externalExchangeRatesService.getExchangeRates());
    }
}
