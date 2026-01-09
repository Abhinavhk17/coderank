package com.coderank.api.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "code_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeSubmission {

    @Id
    private String id;

    private String userId;

    private Language language;

    private String code;

    private SubmissionStatus status = SubmissionStatus.PENDING;

    private String output;

    private String errorMessage;

    private Long executionTimeMs;

    private Long memoryUsedKb;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

