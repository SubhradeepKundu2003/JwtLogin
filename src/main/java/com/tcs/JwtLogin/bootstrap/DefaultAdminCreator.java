package com.tcs.JwtLogin.bootstrap;

import com.tcs.JwtLogin.models.Role;
import com.tcs.JwtLogin.models.User;
import com.tcs.JwtLogin.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DefaultAdminCreator implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultAdminCreator(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // If admin already exists → stop
        if (userRepository.existsByRole(Role.ROLE_ADMIN)) {
            return;
        }

        // Generate random credentials
        String email = "admin_" + UUID.randomUUID().toString().substring(0, 6) + "@system.local";
        String rawPassword = UUID.randomUUID().toString().substring(0, 12);

        User admin = new User();
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setRole(Role.ROLE_ADMIN);
        admin.setEnabled(true);

        userRepository.save(admin);

        // Console only (shown ONCE)
        System.out.println("======================================");
        System.out.println("DEFAULT ADMIN CREATED");
        System.out.println("Email    : " + email);
        System.out.println("Password : " + rawPassword);
        System.out.println("======================================");
    }
}
