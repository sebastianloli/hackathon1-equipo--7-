package com.example.hacka.repository;

import com.example.hacka.entity.ReportRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReportRequestRepository extends JpaRepository<ReportRequest, Long> {
    Optional<ReportRequest> findByRequestId(String requestId);
}
