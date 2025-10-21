package com.example.hacka.service;

import com.example.hacka.dto.RestrictionDTO;
import com.example.hacka.entity.Company;
import com.example.hacka.entity.Restriction;
import com.example.hacka.repository.CompanyRepository;
import com.example.hacka.repository.RestrictionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestrictionService {

    @Autowired
    private RestrictionRepository restrictionRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Transactional
    public RestrictionDTO createRestriction(RestrictionDTO dto) {
        Company company = companyRepository.findById(dto.getCompanyId()).orElse(null);
        if (company == null) return null;

        Restriction restriction = new Restriction();
        restriction.setCompany(company);
        restriction.setModelType(dto.getModelType());
        restriction.setUsageLimit(dto.getUsageLimit());
        restriction = restrictionRepository.save(restriction);

        return toDTO(restriction);
    }

    public List<RestrictionDTO> getRestrictionsByCompany(Long companyId) {
        return restrictionRepository.findByCompanyId(companyId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public RestrictionDTO updateRestriction(Long id, RestrictionDTO dto) {
        Restriction restriction = restrictionRepository.findById(id).orElse(null);
        if (restriction != null) {
            restriction.setModelType(dto.getModelType());
            restriction.setUsageLimit(dto.getUsageLimit());
            restriction = restrictionRepository.save(restriction);
            return toDTO(restriction);
        }
        return null;
    }

    @Transactional
    public void deleteRestriction(Long id) {
        restrictionRepository.deleteById(id);
    }

    private RestrictionDTO toDTO(Restriction restriction) {
        RestrictionDTO dto = new RestrictionDTO();
        dto.setId(restriction.getId());
        dto.setCompanyId(restriction.getCompany().getId());
        dto.setModelType(restriction.getModelType());
        dto.setUsageLimit(restriction.getUsageLimit());
        return dto;
    }
}
