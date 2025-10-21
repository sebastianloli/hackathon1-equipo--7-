package com.example.hacka.repository;

import com.example.hacka.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByUserId(Long userId);

    @Query("SELECT r FROM Request r WHERE r.user.id = ?1 AND r.createdAt >= ?2")
    List<Request> findByUserIdAndCreatedAtAfter(Long userId, LocalDateTime after);

    @Query("SELECT r FROM Request r WHERE r.user.company.id = ?1")
    List<Request> findByCompanyId(Long companyId);
}
