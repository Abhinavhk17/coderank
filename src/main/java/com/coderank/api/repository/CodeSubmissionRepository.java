package com.coderank.api.repository;

import com.coderank.api.domain.CodeSubmission;
import com.coderank.api.domain.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CodeSubmissionRepository extends JpaRepository<CodeSubmission, Long> {
    Page<CodeSubmission> findByUserId(Long userId, Pageable pageable);
    List<CodeSubmission> findByStatus(SubmissionStatus status);
    List<CodeSubmission> findByUserIdAndCreatedAtAfter(Long userId, LocalDateTime date);
    long countByUserIdAndCreatedAtAfter(Long userId, LocalDateTime date);
}

