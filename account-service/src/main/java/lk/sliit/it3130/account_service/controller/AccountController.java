package lk.sliit.it3130.account_service.controller;

import jakarta.validation.Valid;
import lk.sliit.it3130.account_service.dto.RegisterRequest;
import lk.sliit.it3130.account_service.dto.RegisterResponse;
import lk.sliit.it3130.account_service.dto.UpdateProfileRequest;
import lk.sliit.it3130.account_service.dto.UpdateRoleRequest;
import lk.sliit.it3130.account_service.dto.UpdateStatusRequest;
import lk.sliit.it3130.account_service.service.AccountService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = accountService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================
    // Profile Management
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<RegisterResponse> getProfile(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                accountService.getProfile(id, authentication)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegisterResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                accountService.updateProfile(
                        id,
                        request,
                        authentication
                )
        );
    }

    // =========================
    // Admin Operations
    // =========================

    @PatchMapping("/{id}/status")
    public ResponseEntity<RegisterResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {

        return ResponseEntity.ok(
                accountService.updateStatus(id, request)
        );
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<RegisterResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {

        return ResponseEntity.ok(
                accountService.updateRole(id, request)
        );
    }
}