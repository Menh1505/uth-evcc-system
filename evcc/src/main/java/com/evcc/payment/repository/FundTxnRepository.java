package com.evcc.payment.repository;

import com.evcc.payment.entity.FundTxn;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface FundTxnRepository extends JpaRepository<FundTxn, UUID> {
}
