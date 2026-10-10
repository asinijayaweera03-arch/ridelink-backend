package lk.sliit.it3130.account_service.service;

import lk.sliit.it3130.account_service.dto.LoginRequest;
import lk.sliit.it3130.account_service.dto.RegisterRequest;
import lk.sliit.it3130.account_service.exception.DuplicateEmailException;
import lk.sliit.it3130.account_service.exception.InvalidCredentialsException;
import lk.sliit.it3130.account_service.model.Account;
import lk.sliit.it3130.account_service.model.Role;
import lk.sliit.it3130.account_service.repository.AccountRepository;
import lk.sliit.it3130.account_service.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AccountService accountService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();

        registerRequest.setName("Test Passenger");
        registerRequest.setEmail("test@ridelink.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole("PASSENGER");
    }

    // =========================================================
    // REGISTER TESTS
    // =========================================================

    @Test
    void register_savesNewAccount_whenEmailNotTaken() {

        when(accountRepository.existsByEmail("test@ridelink.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashed-password");

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> {
                    Account acc = invocation.getArgument(0);
                    return acc;
                });

        var response = accountService.register(registerRequest);

        assertEquals(
                "test@ridelink.com",
                response.getEmail()
        );

        assertEquals(
                Role.PASSENGER,
                response.getRole()
        );

        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void register_throwsDuplicateEmailException_whenEmailAlreadyExists() {

        when(accountRepository.existsByEmail("test@ridelink.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> accountService.register(registerRequest)
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void register_throwsIllegalArgumentException_whenRoleIsInvalid() {

        registerRequest.setRole("WIZARD");

        when(accountRepository.existsByEmail("test@ridelink.com"))
                .thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> accountService.register(registerRequest)
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    // =========================================================
    // LOGIN TESTS
    // =========================================================

    @Test
    void login_returnsToken_whenCredentialsAreCorrect() {

        Account account = new Account();

        account.setEmail("test@ridelink.com");
        account.setPasswordHash("hashed-password");
        account.setRole(Role.PASSENGER);

        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setEmail("test@ridelink.com");
        loginRequest.setPassword("password123");

        when(accountRepository.findByEmail("test@ridelink.com"))
                .thenReturn(Optional.of(account));

        when(passwordEncoder.matches(
                "password123",
                "hashed-password"
        )).thenReturn(true);

        when(jwtUtil.generateToken(
                "test@ridelink.com",
                "PASSENGER"
        )).thenReturn("fake-jwt-token");

        var response = accountService.login(loginRequest);

        assertEquals(
                "fake-jwt-token",
                response.getToken()
        );

        assertEquals(
                "test@ridelink.com",
                response.getEmail()
        );
    }

    @Test
    void login_throwsInvalidCredentialsException_whenPasswordIsWrong() {

        Account account = new Account();

        account.setEmail("test@ridelink.com");
        account.setPasswordHash("hashed-password");

        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setEmail("test@ridelink.com");
        loginRequest.setPassword("wrongpassword");

        when(accountRepository.findByEmail("test@ridelink.com"))
                .thenReturn(Optional.of(account));

        when(passwordEncoder.matches(
                "wrongpassword",
                "hashed-password"
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> accountService.login(loginRequest)
        );

        verify(jwtUtil, never())
                .generateToken(anyString(), anyString());
    }

    @Test
    void login_throwsInvalidCredentialsException_whenEmailNotFound() {

        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setEmail("nobody@ridelink.com");
        loginRequest.setPassword("password123");

        when(accountRepository.findByEmail("nobody@ridelink.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> accountService.login(loginRequest)
        );

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());
    }
}