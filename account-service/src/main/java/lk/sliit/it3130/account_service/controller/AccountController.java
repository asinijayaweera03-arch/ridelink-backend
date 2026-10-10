package lk.sliit.it3130.account_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(
        name = "Account Management",
        description = "Account registration and profile management APIs"
)
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // =========================
    // Public Registration
    // =========================

    @Operation(
            summary = "Register a new account",
            description = "Creates a new passenger, driver, or admin account."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Account created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists"
            )
    })
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

    @Operation(
            summary = "Get account profile",
            description = "Retrieves an account profile. The account owner or an admin can access it."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<RegisterResponse> getProfile(
            @Parameter(
                    description = "Account ID",
                    example = "1"
            )
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                accountService.getProfile(id, authentication)
        );
    }

    @Operation(
            summary = "Update account profile",
            description = "Updates the account profile name. The account owner or an admin can perform this operation."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<RegisterResponse> updateProfile(
            @Parameter(
                    description = "Account ID",
                    example = "1"
            )
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

    @Operation(
            summary = "Update account status",
            description = "Updates an account status. This operation requires ADMIN role."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin role required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<RegisterResponse> updateStatus(
            @Parameter(
                    description = "Account ID",
                    example = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {

        return ResponseEntity.ok(
                accountService.updateStatus(id, request)
        );
    }

    @Operation(
            summary = "Update account role",
            description = "Updates an account role. This operation requires ADMIN role."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account role updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid role"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin role required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
    @PatchMapping("/{id}/role")
    public ResponseEntity<RegisterResponse> updateRole(
            @Parameter(
                    description = "Account ID",
                    example = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {

        return ResponseEntity.ok(
                accountService.updateRole(id, request)
        );
    }
}