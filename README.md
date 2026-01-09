# CodeRank - Code Execution API

Secure REST API for executing Java and JavaScript code with JWT authentication, rate limiting, and MongoDB storage.

## Quick Start

```bash
# Using Docker (Recommended)
docker-compose up -d

# Or run locally
./mvnw spring-boot:run
```

Application runs on `http://localhost:8081`

## Tech Stack

- **Backend**: Spring Boot 3.5.9, Java 21
- **Database**: MongoDB 7.0
- **Security**: JWT, Spring Security, Bucket4j
- **Build**: Maven


## Prerequisites

- Java 21+
- Maven 3.9+
- MongoDB 7.0+ (or use Docker)
- Optional: Node.js (JavaScript), JDK (Java compilation)

## Installation

### Docker Setup
```bash
docker-compose up -d
docker-compose logs -f  # View logs
docker-compose down     # Stop services
```

### Local Setup
```bash
# Start MongoDB
docker run -d -p 27017:27017 mongo:7.0

# Run application
./mvnw spring-boot:run
```

## API Endpoints

**Base URL**: `http://localhost:8081/api`

### Authentication
```bash
# Register
POST /auth/register
{
  "username": "user",
  "email": "user@example.com",
  "password": "password"
}

# Login (returns JWT token)
POST /auth/login
{
  "username": "user",
  "password": "password"
}
```

### Code Execution
```bash
# Execute code
POST /execute
Authorization: Bearer <token>
{
  "language": "JAVA",
  "code": "public class Main { public static void main(String[] args) { System.out.println(\"Hello\"); } }",
  "input": ""
}

# Get submission by ID
GET /submissions/{id}
Authorization: Bearer <token>

# List user submissions
GET /submissions?page=0&size=10
Authorization: Bearer <token>
```

### Supported Languages
- `JAVA` - Java 21
- `JAVASCRIPT` - Node.js

## Configuration

Edit `src/main/resources/application.yaml`:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/coderank

jwt:
  secret: your-secret-key
  expiration: 86400000  # 24 hours

execution:
  timeout: 10  # seconds

server:
  port: 8081
```

## Security Features

- JWT authentication
- Code validation (blocks file I/O, network, system calls)
- Rate limiting (10 req/min USER, 100 req/min PREMIUM, 1000 req/min ADMIN)
- Execution timeout (10 seconds)
- Code size limit (10,000 characters)

## Project Structure

```
src/main/java/com/coderank/api/
├── config/           # WebConfig, CORS
├── controller/       # REST endpoints
├── domain/           # MongoDB entities
├── dto/              # Request/Response objects
├── exception/        # Error handling
├── execution/        # Code execution engine
├── ratelimit/        # Rate limiting
├── repository/       # MongoDB repositories
├── security/         # JWT, Spring Security
└── service/          # Business logic
```

## Testing

```bash
# Run tests
./mvnw test

# Use Postman collection
Import postman_collection.json
```

## Examples

**Java:**
```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

**JavaScript:**
```javascript
console.log("Hello, World!");
const sum = [1, 2, 3].reduce((a, b) => a + b, 0);
console.log("Sum:", sum);
```

## Troubleshooting

```bash
# Check health
curl http://localhost:8081/api/health

# View logs
docker-compose logs -f coderank-app

# Restart services
docker-compose restart

# Access MongoDB
docker exec -it coderank-mongodb mongosh coderank
```

## License

MIT



