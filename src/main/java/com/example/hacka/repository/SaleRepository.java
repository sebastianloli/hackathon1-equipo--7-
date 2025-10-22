package com.example.hacka.repository;

import com.example.hacka.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByBranch(String branch);

    @Query("SELECT s FROM Sale s WHERE s.soldAt BETWEEN ?1 AND ?2")
    List<Sale> findBySoldAtBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT s FROM Sale s WHERE s.branch = ?1 AND s.soldAt BETWEEN ?2 AND ?3")
    List<Sale> findByBranchAndSoldAtBetween(String branch, LocalDateTime from, LocalDateTime to);
}

