package com.evcc.evcc.repository;

import com.evcc.evcc.entity.FundAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FundAccountRepository extends JpaRepository<FundAccount, UUID> {
}