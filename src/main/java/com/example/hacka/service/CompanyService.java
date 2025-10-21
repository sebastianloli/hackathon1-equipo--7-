package com.example.hacka.service;

import com.example.hacka.dto.CompanyDTO;
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
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public CompanyDTO createCompany(CompanyDTO dto, String adminUsername, String adminPassword) {
        Company company = new Company();
        company.setName(dto.getName());
        company.setRuc(dto.getRuc());
        company.setAffiliationDate(dto.getAffiliationDate());
        company.setActive(true);
        company = companyRepository.save(company);

        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setEmail(adminUsername + "@" + dto.getName().toLowerCase() + ".com");
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(User.Role.ROLE_COMPANY_ADMIN);
        admin.setCompany(company);
        userRepository.save(admin);

        return toDTO(company);
    }

    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CompanyDTO getCompanyById(Long id) {
        return companyRepository.findById(id).map(this::toDTO).orElse(null);
    }

    @Transactional
    public CompanyDTO updateCompany(Long id, CompanyDTO dto) {
        Company company = companyRepository.findById(id).orElse(null);
        if (company != null) {
            company.setName(dto.getName());
            company.setRuc(dto.getRuc());
            company = companyRepository.save(company);
            return toDTO(company);
        }
        return null;
    }

    @Transactional
    public void toggleCompanyStatus(Long id) {
        Company company = companyRepository.findById(id).orElse(null);
        if (company != null) {
            company.setActive(!company.getActive());
            companyRepository.save(company);
        }
    }

    private CompanyDTO toDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setRuc(company.getRuc());
        dto.setAffiliationDate(company.getAffiliationDate());
        dto.setActive(company.getActive());
        return dto;
    }
}
