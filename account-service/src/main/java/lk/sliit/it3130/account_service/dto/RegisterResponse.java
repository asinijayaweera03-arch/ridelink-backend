package lk.sliit.it3130.account_service.dto;

import lk.sliit.it3130.account_service.model.Account;
import lk.sliit.it3130.account_service.model.AccountStatus;
import lk.sliit.it3130.account_service.model.Role;

import java.time.LocalDateTime;

public class RegisterResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private AccountStatus status;
    private LocalDateTime createdAt;

    public RegisterResponse(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.email = account.getEmail();
        this.role = account.getRole();
        this.status = account.getStatus();
        this.createdAt = account.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}