package com.evcc.payment.service;

import com.evcc.payment.entity.PaymentWebhook;
import com.evcc.payment.repository.PaymentWebhookRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




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
