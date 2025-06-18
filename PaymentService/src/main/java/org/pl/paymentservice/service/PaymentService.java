package org.pl.paymentservice.service;

import org.pl.paymentservice.entity.Payment;
import org.pl.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public Payment processPayment(Payment payment) {
        //TODO processing the payment, validation etc.
        return paymentRepository.save(payment);
    }
}

