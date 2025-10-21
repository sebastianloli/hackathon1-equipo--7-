package com.example.hacka.service;

import com.example.hacka.dto.UserDTO;
import com.example.hacka.entity.Company;
import com.example.hacka.entity.User;
import com.example.hacka.repository.CompanyRepository;
import com.example.hacka.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUser() {
        Company company = new Company();
        company.setId(1L);

        UserDTO dto = new UserDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@test.com");
        dto.setRole("ROLE_USER");
        dto.setCompanyId(1L);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setRole(User.Role.ROLE_USER);
        user.setCompany(company);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.createUser(dto, "password");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@test.com", result.getEmail());
        assertEquals("ROLE_USER", result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
