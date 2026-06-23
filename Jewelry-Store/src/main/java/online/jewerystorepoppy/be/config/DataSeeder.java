package online.jewerystorepoppy.be.config;

import online.jewerystorepoppy.be.entity.Account;
import online.jewerystorepoppy.be.enums.AccountStatus;
import online.jewerystorepoppy.be.enums.Role;
import online.jewerystorepoppy.be.repository.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@Profile({"docker", "dev"})  // never runs in "prod"
public class DataSeeder implements CommandLineRunner {

    private final AuthenticationRepository authenticationRepository; //Basically account repository
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataSeeder(AuthenticationRepository authenticationRepository, PasswordEncoder passwordEncoder) {
        this.authenticationRepository = authenticationRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) throws Exception {
        if (authenticationRepository.count() > 0) {
            return; // already seeded, skip
        }

        seedAccount("Admin User", "admin@jewelry.com", "0900000001", "admin123", Role.ADMIN);
        seedAccount("Manager User", "manager@jewelry.com", "0900000002", "manager123", Role.MANAGER);
        seedAccount("Staff User", "staff@jewelry.com", "0900000003", "staff123", Role.STAFF);
        seedAccount("Customer User", "customer@jewelry.com", "0900000004", "customer123", Role.CUSTOMER);
    }

    private void seedAccount(String fullName, String email, String phone, String rawPassword, Role role) {
        Account account = new Account();
        account.setFullName(fullName);
        account.setEmail(email);
        account.setPhone(phone);
        account.setPassword(passwordEncoder.encode(rawPassword));
        account.setRole(role);
        account.setAccountStatus(AccountStatus.ACTIVE);

        authenticationRepository.save(account);
    }
}
