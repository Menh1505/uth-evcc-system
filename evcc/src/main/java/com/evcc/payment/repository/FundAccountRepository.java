package com.evcc.payment.repository;

import com.evcc.payment.entity.FundAccount;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface FundAccountRepository extends JpaRepository<FundAccount, UUID> {
}
