package com.example.ledger.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findByFromAccountOrToAccountOrderByIdAsc(String fromAccount, String toAccount);
}
