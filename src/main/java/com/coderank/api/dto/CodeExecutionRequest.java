package com.coderank.api.dto;

import com.coderank.api.domain.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeExecutionRequest {
    @NotNull(message = "Language is required")
    private Language language;

    @NotBlank(message = "Code is required")
    private String code;

    private String input; // Optional stdin input
}

