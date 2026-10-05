package com.example.ledger.statement;

import com.example.ledger.domain.Account;
import com.example.ledger.domain.AccountRepository;
import com.example.ledger.domain.Transfer;
import com.example.ledger.domain.TransferRepository;
import com.example.ledger.service.TransferService.AccountNotFoundException;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

@Service
public class StatementService {

    private final AccountRepository accounts;
    private final TransferRepository transfers;
    private final JAXBContext context;

    public StatementService(AccountRepository accounts, TransferRepository transfers) throws JAXBException {
        this.accounts = accounts;
        this.transfers = transfers;
        this.context = JAXBContext.newInstance(StatementDocument.class);
    }

    public String exportXml(String accountNumber) {
        Account account = accounts.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        StatementDocument doc = new StatementDocument();
        doc.accountNumber = account.getAccountNumber();
        doc.currency = account.getCurrency();
        doc.holderName = account.getHolderName();
        doc.closingBalance = account.getBalance();
        for (Transfer t : transfers.findByFromAccountOrToAccountOrderByIdAsc(accountNumber, accountNumber)) {
            StatementDocument.Line line = new StatementDocument.Line();
            boolean debit = t.getFromAccount().equals(accountNumber);
            line.id = t.getId();
            line.direction = debit ? "DEBIT" : "CREDIT";
            line.counterparty = debit ? t.getToAccount() : t.getFromAccount();
            line.amount = t.getAmount();
            line.reference = t.getReference();
            line.postedAt = t.getCreatedAt().toString();
            doc.lines.add(line);
        }

        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter out = new StringWriter();
            marshaller.marshal(doc, out);
            return out.toString();
        } catch (JAXBException e) {
            throw new IllegalStateException("Cannot export statement for " + accountNumber, e);
        }
    }
}
