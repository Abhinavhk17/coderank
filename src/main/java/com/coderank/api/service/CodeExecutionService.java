package com.coderank.api.service;

import com.coderank.api.domain.CodeSubmission;
import com.coderank.api.domain.SubmissionStatus;
import com.coderank.api.domain.User;
import com.coderank.api.dto.CodeExecutionRequest;
import com.coderank.api.dto.CodeExecutionResponse;
import com.coderank.api.execution.CodeValidator;
import com.coderank.api.execution.LocalExecutionService;
import com.coderank.api.execution.ExecutionRequest;
import com.coderank.api.execution.ExecutionResult;
import com.coderank.api.repository.CodeSubmissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CodeExecutionService {

    @Autowired
    private CodeSubmissionRepository submissionRepository;

    @Autowired
    private LocalExecutionService localExecutionService;

    @Autowired
    private CodeValidator codeValidator;

    @Transactional
    public CodeExecutionResponse executeCode(CodeExecutionRequest request) {
        User user = getCurrentUser();

        // Validate code for security issues
        codeValidator.validate(request.getCode(), request.getLanguage().name());

        // Create submission record
        CodeSubmission submission = CodeSubmission.builder()
            .userId(user.getId())
            .language(request.getLanguage())
            .code(request.getCode())
            .status(SubmissionStatus.PENDING)
            .build();

        submission = submissionRepository.save(submission);

        // Execute asynchronously
        final Long submissionId = submission.getId();
        CompletableFuture.runAsync(() -> executeAsync(submissionId, request));

        return mapToResponse(submission);
    }

    private void executeAsync(Long submissionId, CodeExecutionRequest request) {
        CodeSubmission submission = submissionRepository.findById(submissionId).orElseThrow();

        try {
            submission.setStatus(SubmissionStatus.RUNNING);
            submissionRepository.save(submission);

            ExecutionRequest execRequest = ExecutionRequest.builder()
                .language(request.getLanguage())
                .code(request.getCode())
                .input(request.getInput())
                .build();

            ExecutionResult result = localExecutionService.execute(execRequest);

            submission.setOutput(result.getOutput());
            submission.setErrorMessage(result.getError());
            submission.setExecutionTimeMs(result.getExecutionTimeMs());
            submission.setMemoryUsedKb(result.getMemoryUsedKb());
            submission.setCompletedAt(LocalDateTime.now());

            if (result.isTimeout()) {
                submission.setStatus(SubmissionStatus.TIMEOUT);
            } else if (result.getExitCode() == 0) {
                submission.setStatus(SubmissionStatus.COMPLETED);
            } else {
                submission.setStatus(SubmissionStatus.FAILED);
            }

        } catch (Exception e) {
            log.error("Error executing code", e);
            submission.setStatus(SubmissionStatus.FAILED);
            submission.setErrorMessage(e.getMessage());
            submission.setCompletedAt(LocalDateTime.now());
        }

        submissionRepository.save(submission);
    }

    public CodeExecutionResponse getSubmission(Long id) {
        User user = getCurrentUser();
        CodeSubmission submission = submissionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Submission not found"));

        if (!submission.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return mapToResponse(submission);
    }

    public Page<CodeExecutionResponse> getUserSubmissions(Pageable pageable) {
        User user = getCurrentUser();
        return submissionRepository.findByUserId(user.getId(), pageable)
            .map(this::mapToResponse);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    private CodeExecutionResponse mapToResponse(CodeSubmission submission) {
        return CodeExecutionResponse.builder()
            .submissionId(submission.getId())
            .language(submission.getLanguage())
            .status(submission.getStatus())
            .output(submission.getOutput())
            .errorMessage(submission.getErrorMessage())
            .executionTimeMs(submission.getExecutionTimeMs())
            .memoryUsedKb(submission.getMemoryUsedKb())
            .createdAt(submission.getCreatedAt())
            .completedAt(submission.getCompletedAt())
            .build();
    }
}

