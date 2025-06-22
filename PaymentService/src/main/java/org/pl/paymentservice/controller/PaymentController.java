package org.pl.paymentservice.controller;

import org.pl.paymentservice.InvoiceDTO;
import org.pl.paymentservice.entity.Payment;
import org.pl.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/")
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
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

        List<InvoiceDTO.ClientInfo> clientInfos = payment.getClients().stream()
                .map(c -> new InvoiceDTO.ClientInfo(c.getId(), c.getName(), c.getClientType()))
                .collect(Collectors.toList());

        invoice.setClients(clientInfos);

        return ResponseEntity.ok(invoice);
    }
}
