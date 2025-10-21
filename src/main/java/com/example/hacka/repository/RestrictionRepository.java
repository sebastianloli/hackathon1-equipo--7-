package com.example.hacka.repository;

import com.example.hacka.entity.Restriction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestrictionRepository extends JpaRepository<Restriction, Long> {
    List<Restriction> findByCompanyId(Long companyId);
}
