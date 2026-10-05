package com.example.ledger.web;

import com.example.ledger.domain.Transfer;
import com.example.ledger.service.TransferService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transfer create(@Valid @RequestBody TransferRequest request) {
        return transferService.transfer(request.getFromAccount(), request.getToAccount(),
                request.getAmount(), request.getReference());
    }
}
