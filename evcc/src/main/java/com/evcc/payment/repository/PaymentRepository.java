package com.evcc.payment.repository;

import com.evcc.payment.entity.Payment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
