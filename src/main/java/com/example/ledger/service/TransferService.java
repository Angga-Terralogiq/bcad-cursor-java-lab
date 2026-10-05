package com.example.ledger.service;

import com.example.ledger.domain.Account;
import com.example.ledger.domain.AccountRepository;
import com.example.ledger.domain.Transfer;
import com.example.ledger.domain.TransferRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TransferService {

    private final AccountRepository accounts;
    private final TransferRepository transfers;
    private final PayloadSigner signer;
    private final Clock clock = Clock.systemDefaultZone();

    public TransferService(AccountRepository accounts, TransferRepository transfers, PayloadSigner signer) {
        this.accounts = accounts;
        this.transfers = transfers;
        this.signer = signer;
    }

    @Transactional
    public Transfer transfer(String fromNumber, String toNumber, BigDecimal amount, String reference) {
        if (fromNumber.equals(toNumber)) {
            throw new TransferRejectedException("SAME_ACCOUNT", "Source and destination accounts are the same");
        }
        Account from = accounts.findByAccountNumber(fromNumber)
                .orElseThrow(() -> new AccountNotFoundException(fromNumber));
        Account to = accounts.findByAccountNumber(toNumber)
                .orElseThrow(() -> new AccountNotFoundException(toNumber));
        BigDecimal value = amount.setScale(2, RoundingMode.UNNECESSARY);
        if (from.getBalance().compareTo(value) < 0) {
            throw new TransferRejectedException("INSUFFICIENT_FUNDS", "Balance is lower than the transfer amount");
        }
        from.debit(value);
        to.credit(value);
        String ref = reference == null ? "" : reference;
        String signature = signer.sign(canonicalPayload(fromNumber, toNumber, value, ref));
        return transfers.save(new Transfer(fromNumber, toNumber, value, ref, signature, LocalDateTime.now(clock)));
    }

    /** The exact string the clearing system re-signs. Field order and format are part of the contract. */
    static String canonicalPayload(String from, String to, BigDecimal amount, String reference) {
        return from + "|" + to + "|" + amount.toPlainString() + "|" + reference;
    }

    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String accountNumber) {
            super("Account " + accountNumber + " not found");
        }
    }

    public static class TransferRejectedException extends RuntimeException {
        private final String code;

        public TransferRejectedException(String code, String message) {
            super(message);
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
