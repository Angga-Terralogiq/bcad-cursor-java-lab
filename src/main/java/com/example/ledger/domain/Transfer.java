package com.example.ledger.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "from_account", nullable = false, length = 10)
    private String fromAccount;

    @Column(name = "to_account", nullable = false, length = 10)
    private String toAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(length = 35)
    private String reference;

    @Column(nullable = false, length = 64)
    private String signature;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Transfer() {
    }

    public Transfer(String fromAccount, String toAccount, BigDecimal amount, String reference,
                    String signature, LocalDateTime createdAt) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.reference = reference;
        this.signature = signature;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReference() {
        return reference;
    }

    public String getSignature() {
        return signature;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
