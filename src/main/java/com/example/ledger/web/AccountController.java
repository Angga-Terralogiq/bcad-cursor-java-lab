package com.example.ledger.web;

import com.example.ledger.domain.Account;
import com.example.ledger.domain.AccountRepository;
import com.example.ledger.service.TransferService.AccountNotFoundException;
import com.example.ledger.statement.StatementService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountRepository accounts;
    private final StatementService statements;

    public AccountController(AccountRepository accounts, StatementService statements) {
        this.accounts = accounts;
        this.statements = statements;
    }

    @GetMapping
    public List<Account> list() {
        return accounts.findAll();
    }

    @GetMapping("/{accountNumber}")
    public Account get(@PathVariable String accountNumber) {
        return accounts.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    @GetMapping(value = "/{accountNumber}/statement", produces = MediaType.APPLICATION_XML_VALUE)
    public String statement(@PathVariable String accountNumber) {
        return statements.exportXml(accountNumber);
    }
}
