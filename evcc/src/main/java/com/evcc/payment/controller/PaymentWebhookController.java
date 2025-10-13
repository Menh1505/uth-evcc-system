package com.evcc.payment.controller;

import com.evcc.payment.entity.PaymentWebhook;
import com.evcc.payment.service.PaymentWebhookService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/payment-webhooks")
public class PaymentWebhookController {
    @Autowired
    private PaymentWebhookService paymentWebhookService;

    @GetMapping
    public List<PaymentWebhook> getAllPaymentWebhooks() {
        return paymentWebhookService.getAllPaymentWebhooks();
    }

    @PostMapping
    public PaymentWebhook savePaymentWebhook(@RequestBody PaymentWebhook paymentWebhook) {
        return paymentWebhookService.savePaymentWebhook(paymentWebhook);
    }

    @GetMapping("/{id}")
    public PaymentWebhook getPaymentWebhookById(@PathVariable UUID id) {
        return paymentWebhookService.getPaymentWebhookById(id);
    }
}
