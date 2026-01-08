package com.coderank.api.execution;

import com.coderank.api.domain.Language;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionRequest {
    private Language language;
    private String code;
    private String input;
}

