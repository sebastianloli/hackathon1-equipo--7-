package com.example.hacka.repository;

import com.example.hacka.entity.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CompanyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void testSaveCompany() {
        Company company = new Company();
        company.setName("Test Corp");
        company.setRuc("20123456789");
        company.setAffiliationDate(LocalDateTime.now());
        company.setActive(true);

        Company saved = companyRepository.save(company);

        assertNotNull(saved.getId());
        assertEquals("Test Corp", saved.getName());
        assertEquals("20123456789", saved.getRuc());
    }

    @Test
    void testFindById() {
        Company company = new Company();
        company.setName("Test Corp");
        company.setRuc("20123456789");
        company.setAffiliationDate(LocalDateTime.now());
        company.setActive(true);

        entityManager.persist(company);
        entityManager.flush();

        Company found = companyRepository.findById(company.getId()).orElse(null);

        assertNotNull(found);
        assertEquals(company.getName(), found.getName());
    }

    @Test
    void testFindAll() {
        Company company1 = new Company();
        company1.setName("Company 1");
        company1.setRuc("20111111111");
        company1.setAffiliationDate(LocalDateTime.now());
        company1.setActive(true);

        Company company2 = new Company();
        company2.setName("Company 2");
        company2.setRuc("20222222222");
        company2.setAffiliationDate(LocalDateTime.now());
        company2.setActive(true);

        entityManager.persist(company1);
        entityManager.persist(company2);
        entityManager.flush();

        var companies = companyRepository.findAll();

        assertTrue(companies.size() >= 2);
    }

    @Test
    void testDeleteCompany() {
        Company company = new Company();
        company.setName("Test Corp");
        company.setRuc("20123456789");
        company.setAffiliationDate(LocalDateTime.now());
        company.setActive(true);

        entityManager.persist(company);
        entityManager.flush();

        companyRepository.deleteById(company.getId());

        Company deleted = companyRepository.findById(company.getId()).orElse(null);
        assertNull(deleted);
    }
}
