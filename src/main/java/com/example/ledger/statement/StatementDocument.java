package com.example.ledger.statement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/** XML statement consumed by the bank's reconciliation batch. */
@XmlRootElement(name = "statement")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatementDocument {

    @XmlAttribute(name = "account")
    String accountNumber;

    @XmlAttribute
    String currency;

    @XmlElement(name = "holder")
    String holderName;

    @XmlElement(name = "closingBalance")
    BigDecimal closingBalance;

    @XmlElementWrapper(name = "lines")
    @XmlElement(name = "line")
    List<Line> lines = new ArrayList<>();

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Line {

        @XmlAttribute
        Long id;

        @XmlAttribute
        String direction;

        @XmlElement
        String counterparty;

        @XmlElement
        BigDecimal amount;

        @XmlElement
        String reference;

        @XmlElement
        String postedAt;
    }
}
