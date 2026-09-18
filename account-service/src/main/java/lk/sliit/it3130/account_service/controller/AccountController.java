package lk.sliit.it3130.account_service.controller;

import jakarta.validation.Valid;
import lk.sliit.it3130.account_service.dto.RegisterRequest;
import lk.sliit.it3130.account_service.dto.RegisterResponse;
import lk.sliit.it3130.account_service.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = accountService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}