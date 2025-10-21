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
        if (userRepository.findByUsername("superadmin").isEmpty()) {
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setEmail("superadmin@sparky.com");
            superAdmin.setPassword(passwordEncoder.encode("admin123"));
            superAdmin.setRole(User.Role.ROLE_SPARKY_ADMIN);
            superAdmin.setCompany(null);
            userRepository.save(superAdmin);

            System.out.println("===========================================");
            System.out.println("Super Admin creado exitosamente!");
            System.out.println("Username: superadmin");
            System.out.println("Password: admin123");
            System.out.println("===========================================");
        } else {
            System.out.println("===========================================");
            System.out.println("Super Admin ya existe en la base de datos");
            System.out.println("Username: superadmin");
            System.out.println("Password: admin123");
            System.out.println("===========================================");
        }
    }
}
