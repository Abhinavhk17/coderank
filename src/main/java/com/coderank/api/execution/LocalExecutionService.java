package com.coderank.api.execution;

import com.coderank.api.domain.Language;
import com.coderank.api.exception.ExecutionTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class LocalExecutionService {

    @Value("${execution.timeout:10}")
    private long executionTimeout;

    public ExecutionResult execute(ExecutionRequest request) {
        long startTime = System.currentTimeMillis();
        Path tempDir = null;

        try {
            // Check if language runtime is available
            String runtimeCheck = checkLanguageRuntime(request.getLanguage());
            if (runtimeCheck != null) {
                return ExecutionResult.builder()
                    .output("")
                    .error(runtimeCheck)
                    .executionTimeMs(System.currentTimeMillis() - startTime)
                    .memoryUsedKb(0)
                    .timeout(false)
                    .exitCode(-1)
                    .build();
            }

            // Create temporary directory and file
            tempDir = Files.createTempDirectory("coderank_");
            String fileName = getFileName(request.getLanguage(), request.getCode());
            Path codeFile = tempDir.resolve(fileName);
            Files.writeString(codeFile, request.getCode());

            // Get execution command based on language
            String[] command = getExecutionCommand(request.getLanguage(), fileName, tempDir);

            // Execute the process
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(tempDir.toFile());
            processBuilder.redirectErrorStream(false);

            Process process = processBuilder.start();

            // Handle input if provided
            if (request.getInput() != null && !request.getInput().isEmpty()) {
                process.getOutputStream().write(request.getInput().getBytes());
                process.getOutputStream().flush();
                process.getOutputStream().close();
            }

            // Wait for completion with timeout
            boolean finished = process.waitFor(executionTimeout, TimeUnit.SECONDS);
            long executionTime = System.currentTimeMillis() - startTime;

            if (!finished) {
                process.destroyForcibly();
                throw new ExecutionTimeoutException("Execution exceeded timeout of " + executionTimeout + " seconds");
            }

            // Read output and error streams
            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    error.append(line).append("\n");
                }
            }

            int exitCode = process.exitValue();

            return ExecutionResult.builder()
                .output(output.toString())
                .error(error.toString())
                .executionTimeMs(executionTime)
                .memoryUsedKb(0) // Not measuring memory in local execution
                .timeout(false)
                .exitCode(exitCode)
                .build();

        } catch (ExecutionTimeoutException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error executing code", e);
            return ExecutionResult.builder()
                .output("")
                .error("Execution failed: " + e.getMessage())
                .executionTimeMs(System.currentTimeMillis() - startTime)
                .memoryUsedKb(0)
                .timeout(false)
                .exitCode(-1)
                .build();
        } finally {
            // Cleanup temporary files
            if (tempDir != null) {
                try {
                    Files.walk(tempDir)
                        .sorted((a, b) -> -a.compareTo(b))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (Exception e) {
                                log.warn("Failed to delete: " + path, e);
                            }
                        });
                } catch (Exception e) {
                    log.error("Failed to cleanup temp directory", e);
                }
            }
        }
    }

    private String[] getExecutionCommand(Language language, String fileName, Path workDir) {
        return switch (language) {
            case PYTHON -> new String[]{"python", fileName};
            case JAVA -> {
                String className = fileName.replace(".java", "");
                yield new String[]{"cmd", "/c", "javac " + fileName + " && java " + className};
            }
            case JAVASCRIPT -> new String[]{"node", fileName};
            case CPP -> new String[]{"cmd", "/c", "g++ " + fileName + " -o program.exe && program.exe"};
        };
    }

    private String getFileName(Language language, String code) {
        if (language == Language.JAVA) {
            // Extract public class name from Java code
            Pattern pattern = Pattern.compile("public\\s+class\\s+(\\w+)");
            Matcher matcher = pattern.matcher(code);
            if (matcher.find()) {
                return matcher.group(1) + ".java";
            }
            // If no public class found, try to find any class
            pattern = Pattern.compile("class\\s+(\\w+)");
            matcher = pattern.matcher(code);
            if (matcher.find()) {
                return matcher.group(1) + ".java";
            }
        }
        // Default filename for other languages or if class name not found
        return "code." + language.getFileExtension();
    }

    private String checkLanguageRuntime(Language language) {
        try {
            String[] checkCommand = switch (language) {
                case PYTHON -> new String[]{"python", "--version"};
                case JAVA -> new String[]{"javac", "-version"};
                case JAVASCRIPT -> new String[]{"node", "--version"};
                case CPP -> new String[]{"g++", "--version"};
            };

            Process process = new ProcessBuilder(checkCommand)
                .redirectErrorStream(true)
                .start();

            boolean finished = process.waitFor(5, TimeUnit.SECONDS);

            if (!finished || process.exitValue() != 0) {
                return getInstallationMessage(language);
            }

            return null; // Runtime is available

        } catch (Exception e) {
            return getInstallationMessage(language);
        }
    }

    private String getInstallationMessage(Language language) {
        return switch (language) {
            case PYTHON -> """
                Python is not installed or not in PATH.
                
                Installation options:
                1. Download from: https://www.python.org/downloads/
                2. Install via Microsoft Store
                3. Install via Chocolatey: choco install python
                
                After installation, make sure Python is added to your system PATH.
                """;
            case JAVA -> """
                Java Development Kit (JDK) is not installed or not in PATH.
                
                Installation options:
                1. Download OpenJDK from: https://adoptium.net/
                2. Install via Chocolatey: choco install openjdk
                
                After installation, make sure JAVA_HOME is set and javac is in your system PATH.
                """;
            case JAVASCRIPT -> """
                Node.js is not installed or not in PATH.
                
                Installation options:
                1. Download from: https://nodejs.org/
                2. Install via Chocolatey: choco install nodejs
                
                After installation, make sure node is added to your system PATH.
                """;
            case CPP -> """
                C++ compiler (g++) is not installed or not in PATH.
                
                Installation options:
                1. Install MinGW-w64 from: https://www.mingw-w64.org/
                2. Install via Chocolatey: choco install mingw
                3. Install MSYS2 and then: pacman -S mingw-w64-x86_64-gcc
                
                After installation, make sure g++ is added to your system PATH.
                """;
        };
    }
}


