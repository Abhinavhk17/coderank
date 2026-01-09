package com.coderank.api.controller;

import com.coderank.api.dto.CodeExecutionRequest;
import com.coderank.api.dto.CodeExecutionResponse;
import com.coderank.api.service.CodeExecutionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CodeExecutionController {

    @Autowired
    private CodeExecutionService codeExecutionService;

    @PostMapping("/execute")
    public ResponseEntity<CodeExecutionResponse> executeCode(@Valid @RequestBody CodeExecutionRequest request) {
        return ResponseEntity.ok(codeExecutionService.executeCode(request));
    }

    @GetMapping("/submissions/{id}")
    public ResponseEntity<CodeExecutionResponse> getSubmission(@PathVariable String id) {
        return ResponseEntity.ok(codeExecutionService.getSubmission(id));
    }

    @GetMapping("/submissions")
    public ResponseEntity<Page<CodeExecutionResponse>> getUserSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(codeExecutionService.getUserSubmissions(pageable));
    }
}

