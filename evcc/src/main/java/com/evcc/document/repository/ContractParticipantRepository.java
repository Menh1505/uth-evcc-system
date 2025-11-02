package com.evcc.document.repository;

import com.evcc.document.entity.Contract;
import com.evcc.document.entity.ContractParticipant;
import com.evcc.document.enums.ParticipantStatus;
import com.evcc.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractParticipantRepository extends JpaRepository<ContractParticipant, Long> {

    // Tìm người tham gia bằng hợp đồng và user (khi họ ký)
    Optional<ContractParticipant> findByContractAndUser(Contract contract, User user);
    
    // Đếm xem còn bao nhiêu người CHƯA ký
    long countByContractAndStatus(Contract contract, ParticipantStatus status);
}