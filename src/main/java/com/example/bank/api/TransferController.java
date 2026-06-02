package com.example.bank.api;

import com.example.bank.api.dto.TransferRequest;
import com.example.bank.api.dto.TransferResponse;
import com.example.bank.security.SecurityUtils;
import com.example.bank.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/transfers")
@Profile("!local")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        Long fromUserId = SecurityUtils.getCurrentUserId();
        transferService.transfer(fromUserId, request.getToUserId(), request.getAmount());
        return ResponseEntity.ok(new TransferResponse(
                fromUserId,
                request.getToUserId(),
                request.getAmount(),
                "COMPLETED"
        ));
    }
}
