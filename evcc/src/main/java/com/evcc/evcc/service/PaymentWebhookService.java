package com.evcc.evcc.service;

import com.evcc.evcc.entity.PaymentWebhook;
import com.evcc.evcc.repository.PaymentWebhookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentWebhookService {
    @Autowired
    private PaymentWebhookRepository paymentWebhookRepository;

    public List<PaymentWebhook> getAllPaymentWebhooks() {
        return paymentWebhookRepository.findAll();
    }

    public PaymentWebhook savePaymentWebhook(PaymentWebhook paymentWebhook) {
        return paymentWebhookRepository.save(paymentWebhook);
    }

    public PaymentWebhook getPaymentWebhookById(UUID id) {
        return paymentWebhookRepository.findById(id).orElse(null);
    }
}