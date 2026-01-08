package com.coderank.api.dto;

import com.coderank.api.domain.Language;
import com.coderank.api.domain.SubmissionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeExecutionResponse {
    private Long submissionId;
    private Language language;
    private SubmissionStatus status;
    private String output;
    private String errorMessage;
    private Long executionTimeMs;
    private Long memoryUsedKb;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}

