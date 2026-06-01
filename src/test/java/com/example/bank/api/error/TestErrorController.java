package com.example.bank.api.error;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("test")
@RequestMapping("/api/test/errors")
public class TestErrorController {

    @GetMapping("/not-found")
    public void notFound() {
        throw new BusinessException(HttpStatus.NOT_FOUND, "Resource missing");
    }
}
