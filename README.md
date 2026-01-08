# CodeRank - Secure Online Code Execution Platform

A robust backend system for executing code snippets in multiple programming languages with comprehensive security measures, resource management, and rate limiting built with Spring Boot.

## ⚡ Quick Start

### Run with H2 Database (Development Mode)

```bash
# Clone the repository
git clone <repository-url>
cd coderank

# Build the project
./mvnw clean install -DskipTests

# Run the application
./mvnw spring-boot:run
```

The application will start on `http://localhost:8081`

**Access H2 Database Console:**
- URL: `http://localhost:8081/h2-console`
- JDBC URL: `jdbc:h2:mem:coderank`
- Username: `sa`
- Password: (leave empty)

### Quick Test
```bash
# Test health endpoint
curl http://localhost:8081/api/health
```

## 🚀 Features

### Core Functionality
- **Multi-Language Support**: Execute code in Python, Java, JavaScript, and C++
- **RESTful API**: Clean, well-documented API endpoints
- **Synchronous Execution**: Fast local code execution with security validation
- **Submission History**: Track and retrieve past code submissions with pagination
- **Health Monitoring**: Built-in health check endpoint for service monitoring

### Security
- **JWT Authentication**: Secure token-based authentication with Spring Security
- **Code Validation**: Pattern-based security checks to prevent malicious code execution
- **Input Validation**: Jakarta Validation for request data integrity
- **Security Violations Blocking**: Prevents file system access, network operations, and dangerous imports
- **Role-based Rate Limiting**: Token bucket algorithm using Bucket4j

### Resource Management
- **Execution Timeout**: 10-second default timeout with graceful error handling
- **Code Size Limit**: Maximum 10,000 characters per submission
- **Memory Safety**: Prevents resource-intensive operations through code validation
- **Error Tracking**: Comprehensive error messages and stack trace capturing

### User Management
- **Role-based Access Control**: USER, PREMIUM, and ADMIN roles
- **Rate Limit Tiers**:
  - USER: 10 requests/minute
  - PREMIUM: 100 requests/minute
  - ADMIN: 1000 requests/minute
- **Per-User Rate Limiting**: Individual rate limit buckets per authenticated user

## 🏗️ Architecture

### Technology Stack
- **Backend Framework**: Spring Boot 3.5.9
- **Language**: Java 21
- **Database**: H2 (in-memory for development)
- **Security**: Spring Security 6.x + JWT (jjwt 0.12.3)
- **Rate Limiting**: Bucket4j 8.0.1
- **Validation**: Jakarta Validation
- **Build Tool**: Maven
- **Code Execution**: Local process execution with timeout management

### Project Structure
```
com.coderank.api/
├── config/           # Spring configuration (WebConfig for interceptors)
├── controller/       # REST API controllers
│   ├── AuthController.java
│   ├── CodeExecutionController.java
│   └── HealthController.java
├── domain/          # JPA entities and enums
│   ├── User.java
│   ├── CodeSubmission.java
│   ├── Language.java
│   ├── UserRole.java
│   └── SubmissionStatus.java
├── dto/             # Data Transfer Objects
│   ├── AuthResponse.java
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   ├── CodeExecutionRequest.java
│   ├── CodeExecutionResponse.java
│   └── ErrorResponse.java
├── exception/       # Custom exceptions and global error handler
│   ├── GlobalExceptionHandler.java
│   ├── RateLimitExceededException.java
│   ├── SecurityViolationException.java
│   ├── ExecutionTimeoutException.java
│   └── ResourceLimitExceededException.java
├── execution/       # Code execution engine
│   ├── LocalExecutionService.java
│   ├── CodeValidator.java
│   ├── ExecutionRequest.java
│   └── ExecutionResult.java
├── ratelimit/       # Rate limiting implementation
│   └── RateLimitInterceptor.java
├── repository/      # JPA repositories
├── security/        # JWT and Spring Security config
│   ├── SecurityConfig.java
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── UserDetailsServiceImpl.java
└── service/         # Business logic layer
    ├── AuthService.java
    └── CodeExecutionService.java
```

### Database Schema
- **users**: User accounts with authentication credentials and roles
- **code_submissions**: Code execution records with results and metrics

## 📋 Prerequisites

### Required
- **Java 21+** - Required for running Spring Boot 3.5.9
- **Maven 3.8+** - For building the project

### Optional (for code execution)
To execute code in different languages, you need the respective runtime installed:
- **Python 3.x** - For Python code execution
- **Node.js 18+** - For JavaScript code execution
- **Java 21** - For Java code execution (already required)
- **GCC/G++** - For C++ code execution

**Note**: The application will run without these language runtimes, but code execution will fail with appropriate error messages for missing languages.

## 🔧 Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd coderank
```

### 2. (Optional) Configure Application
The application comes with sensible defaults in `src/main/resources/application.yaml`:
- H2 in-memory database (no setup required)
- Server runs on port 8081
- JWT expiration: 24 hours
- Execution timeout: 10 seconds

**To customize**, edit `application.yaml`:
```yaml
server:
  port: 8081  # Change server port

jwt:
  secret: your-secret-key  # MUST change in production!
  expiration: 86400000  # JWT token validity

execution:
  timeout: 10  # Execution timeout in seconds
```

### 3. Build the Application
```bash
# Clean build
./mvnw clean package

# Skip tests for faster build
./mvnw clean package -DskipTests
```

### 4. Run the Application

**Using Maven:**
```bash
./mvnw spring-boot:run
```

**Using JAR:**
```bash
java -jar target/coderank-0.0.1-SNAPSHOT.jar
```

**On Windows (PowerShell):**
```powershell
.\mvnw.cmd spring-boot:run
```

The application will start on `http://localhost:8081`

### 5. Verify Installation
```bash
# Check health endpoint
curl http://localhost:8081/api/health

# Expected response:
# {"status":"UP","service":"CodeRank API"}
```

## 📚 API Documentation

### Base URL
```
http://localhost:8081/api
```

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huX2RvZS...",
  "type": "Bearer",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huX2RvZS...",
  "type": "Bearer",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER"
}
```

### Code Execution Endpoints

#### Execute Code
```http
POST /api/execute
Authorization: Bearer <token>
Content-Type: application/json

{
  "language": "PYTHON",
  "code": "print('Hello, World!')",
  "input": ""
}

Response: 200 OK
{
  "submissionId": 1,
  "language": "PYTHON",
  "status": "COMPLETED",
  "output": "Hello, World!\n",
  "errorMessage": null,
  "executionTimeMs": 245,
  "memoryUsedKb": 0,
  "createdAt": "2026-01-09T10:30:00",
  "completedAt": "2026-01-09T10:30:00"
}
```

**Error Response (Security Violation):**
```http
Response: 403 FORBIDDEN
{
  "error": "Security Violation",
  "message": "Code contains forbidden operation: import\\s+os",
  "timestamp": "2026-01-09T10:30:00"
}
```

**Error Response (Rate Limit):**
```http
Response: 429 TOO MANY REQUESTS
{
  "error": "Rate Limit Exceeded",
  "message": "Rate limit exceeded. Please try again later.",
  "timestamp": "2026-01-09T10:30:00"
}
```

#### Get Submission by ID
```http
GET /api/submissions/{id}
Authorization: Bearer <token>

Response: 200 OK
{
  "submissionId": 1,
  "language": "PYTHON",
  "status": "COMPLETED",
  "output": "Hello, World!\n",
  "errorMessage": null,
  "executionTimeMs": 245,
  "memoryUsedKb": 0,
  "createdAt": "2026-01-09T10:30:00",
  "completedAt": "2026-01-09T10:30:00"
}
```

#### Get User Submissions (Paginated)
```http
GET /api/submissions?page=0&size=10
Authorization: Bearer <token>

Response: 200 OK
{
  "content": [
    {
      "submissionId": 2,
      "language": "JAVA",
      "status": "COMPLETED",
      "output": "Hello from Java!\n",
      "errorMessage": null,
      "executionTimeMs": 850,
      "memoryUsedKb": 0,
      "createdAt": "2026-01-09T10:25:00",
      "completedAt": "2026-01-09T10:25:01"
    },
    {
      "submissionId": 1,
      "language": "PYTHON",
      "status": "COMPLETED",
      "output": "Hello, World!\n",
      "errorMessage": null,
      "executionTimeMs": 245,
      "memoryUsedKb": 0,
      "createdAt": "2026-01-09T10:20:00",
      "completedAt": "2026-01-09T10:20:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 2,
  "totalPages": 1,
  "last": true,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 2,
  "empty": false
}
```

### Health Check Endpoint

#### Check API Health
```http
GET /api/health

Response: 200 OK
{
  "status": "UP",
  "service": "CodeRank API"
}
```

### Supported Languages

| Language   | Enum Value  | Required Runtime    | File Extension | Executor Command |
|-----------|-------------|---------------------|----------------|------------------|
| Python    | PYTHON      | Python 3.x          | .py            | python           |
| Java      | JAVA        | Java JDK 21+        | .java          | javac + java     |
| JavaScript| JAVASCRIPT  | Node.js 18+         | .js            | node             |
| C++       | CPP         | GCC/G++ compiler    | .cpp           | g++              |

### Submission Status Types

| Status | Description |
|--------|-------------|
| PENDING | Submission created, waiting for execution |
| RUNNING | Code is currently executing |
| COMPLETED | Execution completed successfully (exit code 0) |
| FAILED | Execution failed with compilation or runtime errors |
| TIMEOUT | Execution exceeded time limit (10 seconds) |
| SECURITY_VIOLATION | Code failed security validation before execution |

## 🔒 Security Measures

### Code Validation (Pre-execution)
The system performs pattern-based security validation before executing any code. Dangerous operations are blocked including:

**Python**
- File system operations: `open()`
- OS commands: `import os`, `import subprocess`
- Network access: `import socket`, `import requests`
- Dynamic execution: `exec()`, `eval()`, `compile()`, `__import__`

**Java**
- File I/O: `java.io.File`
- Runtime execution: `Runtime.getRuntime()`
- Process creation: `ProcessBuilder`
- Network operations: `java.net.*`
- System exit: `System.exit()`

**JavaScript**
- File system: `require('fs')`
- Child processes: `require('child_process')`
- Network modules: `require('net')`, `require('http')`
- Dynamic execution: `eval()`, `Function()`

**C++**
- File streams: `#include <fstream>`, `#include <filesystem>`
- System calls: `system()`
- Process creation: `fork()`, `popen()`

### Execution Security
- **Process Isolation**: Code runs in separate local processes
- **Timeout Enforcement**: 10-second hard timeout per execution
- **Code Length Limit**: Maximum 10,000 characters
- **Error Handling**: Safe error message capture without exposing system details

### Application Security
- **JWT Authentication**: Stateless token-based authentication
- **Password Encryption**: BCrypt password hashing
- **CORS Configuration**: Controlled cross-origin resource sharing
- **Input Validation**: Jakarta Bean Validation on all API requests
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries

### Rate Limiting
- **Per-User Buckets**: Individual token buckets per authenticated user
- **Role-Based Limits**: Different quotas for USER, PREMIUM, and ADMIN
- **Token Bucket Algorithm**: Using Bucket4j for efficient rate limiting
- **Automatic Refill**: Rate limits refill every minute
- **Interceptor Pattern**: Applied only to protected endpoints (/api/execute, /api/submissions/**)

### Exception Handling
- **Global Exception Handler**: Centralized error handling with `@RestControllerAdvice`
- **Structured Error Responses**: Consistent error format across all endpoints
- **Security Error Masking**: Internal errors don't expose system information

## 🧪 Testing

### Using Postman Collection
Import the provided `postman_collection.json` file into Postman:

1. Open Postman
2. Click Import → Upload Files
3. Select `postman_collection.json`
4. The collection includes:
   - Environment variables (base_url, token, submission_id)
   - Auto-token saving scripts
   - All API endpoints with examples
   - Error case scenarios

### Manual Testing with cURL

**1. Health Check**
```bash
curl http://localhost:8081/api/health
```

**2. Register a User**
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

**3. Login (Save the token from response)**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**4. Execute Python Code**
```bash
curl -X POST http://localhost:8081/api/execute \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "language": "PYTHON",
    "code": "for i in range(5):\n    print(f\"Number: {i}\")",
    "input": ""
  }'
```

**5. Get Submission by ID**
```bash
curl -X GET http://localhost:8081/api/submissions/1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**6. Get All User Submissions**
```bash
curl -X GET "http://localhost:8081/api/submissions?page=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**7. Test Security Validation (Should Return 403)**
```bash
curl -X POST http://localhost:8081/api/execute \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "language": "PYTHON",
    "code": "import os\nos.system(\"ls\")"
  }'
```

**8. Test Rate Limiting (Run 11+ times quickly)**
```bash
# Run this in a loop to trigger rate limit
for i in {1..15}; do
  curl -X POST http://localhost:8081/api/execute \
    -H "Authorization: Bearer YOUR_TOKEN_HERE" \
    -H "Content-Type: application/json" \
    -d '{"language":"PYTHON","code":"print(\"Test\")"}';
  echo " - Request $i";
done
```

### Example Code Snippets

**Python - Calculate Fibonacci**
```python
def fibonacci(n):
    if n <= 1:
        return n
    return fibonacci(n-1) + fibonacci(n-2)

for i in range(10):
    print(fibonacci(i))
```

**Java - Hello World**
```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello from Java!");
    }
}
```

**JavaScript - Array Operations**
```javascript
const numbers = [1, 2, 3, 4, 5];
const sum = numbers.reduce((acc, num) => acc + num, 0);
console.log("Sum:", sum);
```

**C++ - Simple Math**
```cpp
#include <iostream>
using namespace std;

int main() {
    int a = 5, b = 10;
    cout << "Sum: " << (a + b) << endl;
    return 0;
}
```

## 🎯 Design Decisions

### 1. Synchronous Execution Model
**Decision**: Execute code synchronously and return complete results immediately.

**Rationale**: 
- Simpler client implementation - no polling required
- Lower latency for short-running code
- Easier debugging and testing
- Suitable for educational/demo purposes
- Timeout mechanism prevents hanging requests

**Trade-off**: Not ideal for very long-running code, but mitigated by 10-second timeout.

### 2. Local Process Execution
**Decision**: Use local process execution instead of Docker containers.

**Rationale**:
- Faster execution (no container overhead)
- Simpler deployment (no Docker daemon dependency)
- Lower resource requirements
- Easier to set up for development
- Combined with code validation for security

**Trade-off**: Less isolation than containers, but pattern-based validation provides defense-in-depth.

### 3. JWT Stateless Authentication
**Decision**: Use stateless JWT tokens instead of session-based authentication.

**Rationale**:
- Scalability - no server-side session storage needed
- Suitable for REST API architecture
- Easy integration with frontend applications
- Standard industry practice
- Works well with Spring Security

### 4. Pattern-based Security Validation
**Decision**: Use regex patterns to detect dangerous code before execution.

**Rationale**:
- Fast pre-execution validation
- Low performance overhead
- Easy to extend with new patterns
- Clear error messages for developers
- Catches most common security issues

**Limitations**: Could be bypassed by obfuscation; suitable for trusted/educational environments.

### 5. H2 In-Memory Database
**Decision**: Use H2 database for development/demo instead of PostgreSQL.

**Rationale**:
- Zero configuration required
- Fast startup for development
- Built-in web console for debugging
- Easy testing without external dependencies
- Can be switched to PostgreSQL for production

### 6. Bucket4j In-Memory Rate Limiting
**Decision**: Use in-memory token bucket algorithm for rate limiting.

**Rationale**:
- Fast performance (microsecond latency)
- Simple implementation
- No external dependencies (Redis, etc.)
- Adequate for single-instance deployment
- Role-based limits easy to configure

**Trade-off**: Limits reset on application restart; not shared across instances.

### 7. Role-Based Access Control (RBAC)
**Decision**: Implement USER, PREMIUM, and ADMIN roles with different rate limits.

**Rationale**:
- Supports freemium business model
- Prevents abuse by free-tier users
- Allows admins unlimited access for testing
- Easily extensible for more roles
- Standard Spring Security integration

## 🚧 Future Enhancements

### 1. Execution Improvements
   - **Docker Container Isolation**: Migrate to Docker for stronger security
   - **Asynchronous Execution**: Add async execution with WebSocket notifications
   - **Container Pooling**: Pre-warm containers for faster execution
   - **Multiple Test Cases**: Support running code against multiple test inputs
   - **Resource Metrics**: Add actual memory and CPU usage tracking

### 2. Enhanced Security
   - **AST-based Validation**: Replace regex with Abstract Syntax Tree parsing
   - **Sandboxing**: Implement OS-level sandboxing (seccomp, AppArmor)
   - **Code Scanning**: Add static analysis tools integration
   - **Audit Logging**: Track all code executions with detailed logs

### 3. Database & Persistence
   - **PostgreSQL Migration**: Production-ready database configuration
   - **Flyway Migrations**: Version-controlled database schema
   - **Code Storage**: Store submitted code permanently
   - **Execution Cache**: Cache results for identical code submissions

### 4. Advanced Features
   - **Multiple Language Versions**: Support Python 2/3, Java 11/17/21, etc.
   - **Custom Dependencies**: Allow installing packages/libraries
   - **Collaborative Coding**: Real-time code sharing and execution
   - **Code Templates**: Pre-built templates for common tasks
   - **Leaderboards**: Competitive programming features
   - **Time Complexity Analysis**: Automated algorithm analysis

### 5. Performance & Scalability
   - **Redis Rate Limiting**: Distributed rate limiting for multi-instance deployment
   - **Message Queue**: RabbitMQ/Kafka for async execution
   - **Kubernetes Deployment**: Container orchestration for auto-scaling
   - **Load Balancing**: Horizontal scaling support
   - **CDN Integration**: Static content delivery

### 6. Monitoring & Observability
   - **Prometheus Metrics**: Application performance metrics
   - **Grafana Dashboards**: Visual monitoring and alerting
   - **Distributed Tracing**: OpenTelemetry/Jaeger integration
   - **ELK Stack**: Centralized logging with Elasticsearch
   - **Health Checks**: Advanced readiness and liveness probes

### 7. Developer Experience
   - **OpenAPI/Swagger**: Auto-generated API documentation
   - **API Versioning**: Support multiple API versions
   - **SDK Libraries**: Client libraries for popular languages
   - **Webhooks**: Execution completion notifications
   - **GraphQL API**: Alternative query interface

## 📊 System Limitations

### Current Constraints
- **Code Size**: Maximum 10,000 characters per submission
- **Execution Timeout**: 10 seconds hard limit
- **Concurrent Executions**: Limited by server CPU/memory
- **Rate Limits**: 
  - USER: 10 requests/minute
  - PREMIUM: 100 requests/minute
  - ADMIN: 1000 requests/minute
- **Storage**: In-memory H2 database (data lost on restart)
- **Language Runtimes**: Must be pre-installed on server
- **Memory Tracking**: Not currently measured (shows 0)
- **Single Instance**: No distributed deployment support yet

### Known Limitations
- Code validation uses regex (can potentially be bypassed with obfuscation)
- Local execution lacks strong isolation (suitable for trusted environments)
- Rate limits are per-instance (reset on restart)
- No output size limit (could cause memory issues with large outputs)
- Synchronous execution may timeout for complex operations

## 🐛 Troubleshooting

### Application Won't Start

**Issue: Port already in use**
```bash
# Check what's using port 8081
# On Windows PowerShell:
netstat -ano | findstr :8081

# Kill the process or change port in application.yaml
```

**Issue: Java version mismatch**
```bash
# Check Java version
java -version

# Should be Java 21 or higher
# Download from: https://adoptium.net/
```

**Issue: Maven build fails**
```bash
# Clean Maven cache and rebuild
./mvnw clean install -U

# On Windows:
.\mvnw.cmd clean install -U
```

### Code Execution Issues

**Issue: "Language runtime not found"**
```bash
# Verify language runtime is installed:
python --version    # For Python
node --version      # For JavaScript
java -version       # For Java
g++ --version       # For C++

# Install missing runtime from official websites
```

**Issue: Execution timeout**
- Reduce code complexity
- Check for infinite loops
- Timeout is set to 10 seconds (configurable in application.yaml)

**Issue: Security violation error**
- Review the forbidden patterns in error message
- Remove dangerous imports/operations from code
- Check CodeValidator.java for blocked patterns

### Authentication Issues

**Issue: "Unauthorized" (401)**
- Ensure JWT token is included in Authorization header
- Format: `Authorization: Bearer <your-token>`
- Token expires after 24 hours - login again

**Issue: "Forbidden" (403)**
- Check if endpoint requires specific role
- Verify rate limit hasn't been exceeded
- Review security validation errors

### Rate Limiting Issues

**Issue: "Rate limit exceeded" (429)**
- Wait 1 minute for rate limit to refill
- User role determines limits:
  - USER: 10/minute
  - PREMIUM: 100/minute
  - ADMIN: 1000/minute
- Rate limits reset on application restart

### Database Issues

**Issue: H2 Console not accessible**
```yaml
# Verify in application.yaml:
spring:
  h2:
    console:
      enabled: true
      path: /h2-console
```

**Issue: Data lost after restart**
- H2 in-memory database clears on restart
- This is expected behavior for development
- For persistence, migrate to PostgreSQL

### Development Tips

**Enable detailed logging:**
```yaml
# In application.yaml, change:
logging:
  level:
    com.coderank.api: DEBUG
    org.springframework.security: DEBUG
```

**Check application logs:**
```bash
# Logs are printed to console
# Look for exceptions and error messages
```

**Test without authentication:**
```bash
# Health endpoint doesn't require auth:
curl http://localhost:8081/api/health
```

## 📄 License

This project is created as a case study for educational purposes.

## 👨‍💻 Author

Developed as part of the Backend Engineering Launchpad by Airtribe

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Docker community for containerization tools
- PostgreSQL team for the robust database
- Open source community for various libraries used

---

**Note**: This is a development setup. For production deployment:
- Change JWT secret to a strong, random value
- Use environment variables for sensitive data
- Enable HTTPS/TLS
- Implement proper logging and monitoring
- Use managed database services
- Set up proper backup and disaster recovery
- Consider using Kubernetes for orchestration
- Implement CDN and load balancing


