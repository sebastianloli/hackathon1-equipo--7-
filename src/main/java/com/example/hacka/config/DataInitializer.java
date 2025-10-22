package com.example.hacka.config;

import com.example.hacka.entity.User;
import com.example.hacka.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("oreo.admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("oreo.admin");
            admin.setEmail("admin@oreo.com");
            admin.setPassword(passwordEncoder.encode("Oreo1234"));
            admin.setRole(User.Role.CENTRAL);
            admin.setBranch(null);
            userRepository.save(admin);

            System.out.println("===========================================");
            System.out.println("Usuario CENTRAL creado:");
            System.out.println("Username: oreo.admin");
            System.out.println("Password: Oreo1234");
            System.out.println("===========================================");
        }
    }
}
