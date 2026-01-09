package com.coderank.api.repository;

import com.coderank.api.domain.CodeSubmission;
import com.coderank.api.domain.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CodeSubmissionRepository extends MongoRepository<CodeSubmission, String> {
    Page<CodeSubmission> findByUserId(String userId, Pageable pageable);
    List<CodeSubmission> findByStatus(SubmissionStatus status);
    List<CodeSubmission> findByUserIdAndCreatedAtAfter(String userId, LocalDateTime date);
    long countByUserIdAndCreatedAtAfter(String userId, LocalDateTime date);
}

