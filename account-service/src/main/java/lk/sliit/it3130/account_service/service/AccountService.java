package lk.sliit.it3130.account_service.service;

import lk.sliit.it3130.account_service.dto.RegisterRequest;
import lk.sliit.it3130.account_service.dto.RegisterResponse;
import lk.sliit.it3130.account_service.exception.DuplicateEmailException;
import lk.sliit.it3130.account_service.model.Account;
import lk.sliit.it3130.account_service.model.Role;
import lk.sliit.it3130.account_service.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Role must be one of: PASSENGER, DRIVER, ADMIN");
        }

        Account account = new Account();
        account.setName(request.getName());
        account.setEmail(request.getEmail());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setRole(role);

        Account saved = accountRepository.save(account);
        return new RegisterResponse(saved);
    }
}