package com.example.ledger.web;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class TransferRequest {

    @NotBlank
    @Pattern(regexp = "\\d{10}", message = "must be a 10-digit account number")
    private String fromAccount;

    @NotBlank
    @Pattern(regexp = "\\d{10}", message = "must be a 10-digit account number")
    private String toAccount;

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 15, fraction = 2)
    private BigDecimal amount;

    @Size(max = 35)
    private String reference;

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
