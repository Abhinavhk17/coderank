package com.coderank.api.execution;

import com.coderank.api.domain.Language;
import com.coderank.api.exception.ExecutionTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Local Execution Service Tests")
class LocalExecutionServiceTest {

    @Autowired
    private LocalExecutionService executionService;

    @BeforeEach
    void setUp() {
        // Set timeout to 10 seconds for tests
        ReflectionTestUtils.setField(executionService, "executionTimeout", 10L);
    }

    // Java Execution Tests
    @Test
    @DisplayName("Should execute simple Java code successfully")
    void shouldExecuteSimpleJavaCode() {
        ExecutionRequest request = ExecutionRequest.builder()
            .language(Language.JAVA)
            .code("""
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("Hello from Java!");
                    }
                }
                """)
            .input("")
            .build();

        ExecutionResult result = executionService.execute(request);

        assertNotNull(result);
        assertEquals(0, result.getExitCode());
        assertTrue(result.getOutput().contains("Hello from Java!"));
    }

    @Test
    @DisplayName("Should execute Java code with loops")
    void shouldExecuteJavaCodeWithLoops() {
        ExecutionRequest request = ExecutionRequest.builder()
            .language(Language.JAVA)
            .code("""
                public class Main {
                    public static void main(String[] args) {
                        int sum = 0;
                        for (int i = 1; i <= 10; i++) {
                            sum += i;
                        }
                        System.out.println("Sum: " + sum);
                    }
                }
                """)
            .input("")
            .build();

        ExecutionResult result = executionService.execute(request);

        assertNotNull(result);
        assertEquals(0, result.getExitCode());
        assertTrue(result.getOutput().contains("Sum: 55"));
    }

    @Test
    @DisplayName("Should extract class name from Java code")
    void shouldExtractClassNameFromJavaCode() {
        ExecutionRequest request = ExecutionRequest.builder()
            .language(Language.JAVA)
            .code("""
                public class CustomClass {
                    public static void main(String[] args) {
                        System.out.println("Custom class name!");
                    }
                }
                """)
            .input("")
            .build();

        ExecutionResult result = executionService.execute(request);

        assertNotNull(result);
        assertEquals(0, result.getExitCode());
        assertTrue(result.getOutput().contains("Custom class name!"));
    }

    @Test
    @DisplayName("Should handle Java compilation error")
    void shouldHandleJavaCompilationError() {
        ExecutionRequest request = ExecutionRequest.builder()
            .language(Language.JAVA)
            .code("""
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("Missing semicolon")
                    }
                }
                """)
            .input("")
            .build();

        ExecutionResult result = executionService.execute(request);

        assertNotNull(result);
        assertNotEquals(0, result.getExitCode());
        assertFalse(result.getError().isEmpty());
    }

    // JavaScript Execution Tests
    @Test
    @DisplayName("Should execute simple JavaScript code successfully")
    void shouldExecuteSimpleJavaScriptCode() {
        ExecutionRequest request = ExecutionRequest.builder()
            .language(Language.JAVASCRIPT)
            .code("console.log('Hello from Node.js!');")
            .input("")
            .build();

        ExecutionResult result = executionService.execute(request);

        assertNotNull(result);
        assertEquals(0, result.getExitCode());
        assertTrue(result.getOutput().contains("Hello from Node.js!"));
    }

    @Test
    @DisplayName("Should execute JavaScript code with array operations")
    void shouldExecuteJavaScriptCodeWithArrayOperations() {
        ExecutionRequest request = ExecutionRequest.builder()
            .language(Language.JAVASCRIPT)
            .code("""
                const numbers = [1, 2, 3, 4, 5];
                const sum = numbers.reduce((acc, num) => acc + num, 0);
                console.log('Sum:', sum);
                """)
            .input("")
            .build();

        ExecutionResult result = executionService.execute(request);

        assertNotNull(result);
        assertEquals(0, result.getExitCode());
        assertTrue(result.getOutput().contains("Sum: 15"));
    }

    @Test
    @DisplayName("Should handle JavaScript runtime error")
    void shouldHandleJavaScriptRuntimeError() {
        ExecutionRequest request = ExecutionRequest.builder()
            .language(Language.JAVASCRIPT)
            .code("console.log(undefinedVariable);")
            .input("")
            .build();

        ExecutionResult result = executionService.execute(request);

        assertNotNull(result);
        assertNotEquals(0, result.getExitCode());
        assertFalse(result.getError().isEmpty());
    }
    
}

