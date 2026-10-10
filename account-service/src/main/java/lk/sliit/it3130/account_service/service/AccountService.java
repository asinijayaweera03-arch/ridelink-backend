package lk.sliit.it3130.account_service.service;

import lk.sliit.it3130.account_service.dto.LoginRequest;
import lk.sliit.it3130.account_service.dto.LoginResponse;
import lk.sliit.it3130.account_service.dto.RegisterRequest;
import lk.sliit.it3130.account_service.dto.RegisterResponse;
import lk.sliit.it3130.account_service.dto.UpdateProfileRequest;
import lk.sliit.it3130.account_service.dto.UpdateRoleRequest;
import lk.sliit.it3130.account_service.dto.UpdateStatusRequest;
import lk.sliit.it3130.account_service.exception.AccountNotFoundException;
import lk.sliit.it3130.account_service.exception.DuplicateEmailException;
import lk.sliit.it3130.account_service.exception.ForbiddenException;
import lk.sliit.it3130.account_service.exception.InvalidCredentialsException;
import lk.sliit.it3130.account_service.model.Account;
import lk.sliit.it3130.account_service.model.AccountStatus;
import lk.sliit.it3130.account_service.model.Role;
import lk.sliit.it3130.account_service.repository.AccountRepository;
import lk.sliit.it3130.account_service.security.JwtUtil;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AccountService(
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {

        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Role must be one of: PASSENGER, DRIVER, ADMIN");
        }

        Account account = new Account();
        account.setName(request.getName());
        account.setEmail(request.getEmail());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setRole(role);

        Account saved = accountRepository.save(account);
        return new RegisterResponse(saved);
    }

    public LoginResponse login(LoginRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(
                request.getPassword(),
                account.getPasswordHash())) {

            throw new InvalidCredentialsException();
        }

        String token = jwtUtil.generateToken(
                account.getEmail(),
                account.getRole().name());

        return new LoginResponse(
                token,
                account.getEmail(),
                account.getRole().name());
    }

    // =========================
    // Profile Management
    // =========================

    public RegisterResponse getProfile(
            Long id,
            Authentication authentication) {

        Account account = accountRepository.findById(id)
                .orElseThrow(AccountNotFoundException::new);

        checkOwnerOrAdmin(account, authentication);

        return new RegisterResponse(account);
    }

    public RegisterResponse updateProfile(
            Long id,
            UpdateProfileRequest request,
            Authentication authentication) {

        Account account = accountRepository.findById(id)
                .orElseThrow(AccountNotFoundException::new);

        checkOwnerOrAdmin(account, authentication);

        account.setName(request.getName());

        Account saved = accountRepository.save(account);

        return new RegisterResponse(saved);
    }

    // =========================
    // Admin Operations
    // =========================

    public RegisterResponse updateStatus(
            Long id,
            UpdateStatusRequest request) {

        Account account = accountRepository.findById(id)
                .orElseThrow(AccountNotFoundException::new);

        AccountStatus status;

        try {
            status = AccountStatus.valueOf(
                    request.getStatus().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Status must be one of: ACTIVE, SUSPENDED, DEACTIVATED");
        }

        account.setStatus(status);

        Account saved = accountRepository.save(account);

        return new RegisterResponse(saved);
    }

    public RegisterResponse updateRole(
            Long id,
            UpdateRoleRequest request) {

        Account account = accountRepository.findById(id)
                .orElseThrow(AccountNotFoundException::new);

        Role role;

        try {
            role = Role.valueOf(
                    request.getRole().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Role must be one of: PASSENGER, DRIVER, ADMIN");
        }

        account.setRole(role);

        Account saved = accountRepository.save(account);

        return new RegisterResponse(saved);
    }

    // =========================
    // Authorization
    // =========================

    private void checkOwnerOrAdmin(
            Account account,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner =
                authentication.getName().equals(account.getEmail());

        if (!isAdmin && !isOwner) {
            throw new ForbiddenException();
        }
    }
}