package com.evcc.payment.service;

import com.evcc.payment.entity.Payment;
import com.evcc.payment.repository.PaymentRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(UUID id) {
        return paymentRepository.findById(id).orElse(null);
    }
}
