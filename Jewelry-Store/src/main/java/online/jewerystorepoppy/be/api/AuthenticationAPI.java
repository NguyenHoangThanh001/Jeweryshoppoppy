package online.jewerystorepoppy.be.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import online.jewerystorepoppy.be.entity.Account;
import online.jewerystorepoppy.be.enums.Role;
import online.jewerystorepoppy.be.model.*;
import online.jewerystorepoppy.be.service.AuthenticationService;
import online.jewerystorepoppy.be.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@SecurityRequirement(name = "api")
public class AuthenticationAPI {

    // nhận request từ front-end

    @Autowired
    EmailService emailService;

    @Autowired
    AuthenticationService authenticationService;

    @GetMapping("test") // /api/test
    public ResponseEntity test() {
        return ResponseEntity.ok("test");
    }

    @GetMapping("admin-only") // /api/admin-only
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity getAdmin() {
        return ResponseEntity.ok("ok");
    }

    @PostMapping("register") // /api/register
    public ResponseEntity register(@RequestBody RegisterRequest registerRequest) {
        // account đã add xuống db
        Account account = authenticationService.register(registerRequest);
        return ResponseEntity.ok(account);
    }

    @PostMapping("login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        AccountResponse account = authenticationService.login(loginRequest);
        return ResponseEntity.ok(account);
    }

    @PostMapping("forgot-password")
    public void forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authenticationService.forgotPasswordRequest(forgotPasswordRequest.getEmail());
    }

    @PatchMapping("reset-password")
    public void resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        authenticationService.resetPassword(resetPasswordRequest);
    }

    @PutMapping("account/{id}")
    public ResponseEntity updateAccount(@PathVariable long id, @RequestBody RegisterRequest registerRequest) {
        Account account = authenticationService.update(id, registerRequest);
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("account/{id}")
    public ResponseEntity deleteAccount(@PathVariable long id) {
        Account account = authenticationService.delete(id);
        return ResponseEntity.ok(account);
    }

    @GetMapping("account")
    public ResponseEntity getAllAccount(@RequestParam(required = false) Role role, @RequestParam(required = false) String keyWord) {
        return ResponseEntity.ok(authenticationService.getAllAccount(role, keyWord));
    }
}
