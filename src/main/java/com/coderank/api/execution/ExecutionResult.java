package com.coderank.api.execution;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionResult {
    private String output;
    private String error;
    private long executionTimeMs;
    private long memoryUsedKb;
    private boolean timeout;
    private int exitCode;
}

