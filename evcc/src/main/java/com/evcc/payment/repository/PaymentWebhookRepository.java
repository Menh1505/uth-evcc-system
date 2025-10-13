package com.evcc.payment.repository;

import com.evcc.payment.entity.PaymentWebhook;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface PaymentWebhookRepository extends JpaRepository<PaymentWebhook, UUID> {
}
