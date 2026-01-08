package com.coderank.api.execution;

import com.coderank.api.exception.SecurityViolationException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class CodeValidator {

    // Dangerous patterns that should be blocked
    private static final Map<String, List<Pattern>> DANGEROUS_PATTERNS = new HashMap<>();

    static {
        // Python dangerous patterns
        DANGEROUS_PATTERNS.put("PYTHON", Arrays.asList(
            Pattern.compile("import\\s+os"),
            Pattern.compile("import\\s+subprocess"),
            Pattern.compile("import\\s+socket"),
            Pattern.compile("import\\s+requests"),
            Pattern.compile("__import__"),
            Pattern.compile("exec\\s*\\("),
            Pattern.compile("eval\\s*\\("),
            Pattern.compile("compile\\s*\\("),
            Pattern.compile("open\\s*\\(")
        ));

        // Java dangerous patterns
        DANGEROUS_PATTERNS.put("JAVA", Arrays.asList(
            Pattern.compile("import\\s+java\\.io\\.File"),
            Pattern.compile("import\\s+java\\.lang\\.Runtime"),
            Pattern.compile("import\\s+java\\.lang\\.Process"),
            Pattern.compile("import\\s+java\\.net"),
            Pattern.compile("Runtime\\.getRuntime"),
            Pattern.compile("ProcessBuilder"),
            Pattern.compile("System\\.exit")
        ));

        // JavaScript dangerous patterns
        DANGEROUS_PATTERNS.put("JAVASCRIPT", Arrays.asList(
            Pattern.compile("require\\s*\\(\\s*['\"]fs['\"]"),
            Pattern.compile("require\\s*\\(\\s*['\"]child_process['\"]"),
            Pattern.compile("require\\s*\\(\\s*['\"]net['\"]"),
            Pattern.compile("require\\s*\\(\\s*['\"]http['\"]"),
            Pattern.compile("eval\\s*\\("),
            Pattern.compile("Function\\s*\\(")
        ));

        // C++ dangerous patterns
        DANGEROUS_PATTERNS.put("CPP", Arrays.asList(
            Pattern.compile("#include\\s*<fstream>"),
            Pattern.compile("#include\\s*<filesystem>"),
            Pattern.compile("system\\s*\\("),
            Pattern.compile("popen\\s*\\("),
            Pattern.compile("fork\\s*\\(")
        ));
    }

    public void validate(String code, String language) {
        if (code == null || code.trim().isEmpty()) {
            throw new SecurityViolationException("Code cannot be empty");
        }

        // Check code length (prevent DOS)
        if (code.length() > 10000) {
            throw new SecurityViolationException("Code exceeds maximum length of 10000 characters");
        }

        // Check for dangerous patterns
        List<Pattern> patterns = DANGEROUS_PATTERNS.get(language.toUpperCase());
        if (patterns != null) {
            for (Pattern pattern : patterns) {
                if (pattern.matcher(code).find()) {
                    throw new SecurityViolationException(
                        "Code contains forbidden operation: " + pattern.pattern()
                    );
                }
            }
        }
    }
}

