package com.example.hacka.service;

import com.example.hacka.dto.UserDTO;
import com.example.hacka.entity.Company;
import com.example.hacka.entity.User;
import com.example.hacka.repository.CompanyRepository;
import com.example.hacka.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO createUser(UserDTO dto, String password) {
        Company company = companyRepository.findById(dto.getCompanyId()).orElse(null);
        if (company == null) return null;

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.valueOf(dto.getRole()));
        user.setCompany(company);
        user = userRepository.save(user);

        return toDTO(user);
    }

    public List<UserDTO> getUsersByCompany(Long companyId) {
        return userRepository.findByCompanyId(companyId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id).map(this::toDTO).orElse(null);
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            user = userRepository.save(user);
            return toDTO(user);
        }
        return null;
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setCompanyId(user.getCompany().getId());
        return dto;
    }
}
