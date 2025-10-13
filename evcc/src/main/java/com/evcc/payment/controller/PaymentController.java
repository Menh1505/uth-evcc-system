package com.evcc.payment.controller;

import com.evcc.payment.entity.Payment;
import com.evcc.payment.service.PaymentService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @PostMapping
    public Payment savePayment(@RequestBody Payment payment) {
        return paymentService.savePayment(payment);
    }

    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable UUID id) {
        return paymentService.getPaymentById(id);
    }
}
