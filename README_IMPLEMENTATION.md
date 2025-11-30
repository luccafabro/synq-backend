# Synq Backend - Spring Boot Application

Complete Spring Boot backend with Frequencies (channels), Messages, Users, and Keycloak SSO integration.

## 🏗️ Architecture

### Technology Stack
- **Java 17**
- **Spring Boot 3.4.5**
- **Spring Cloud 2024.0.1**
- **MySQL 8.0+**
- **Flyway** (Database Migrations)
- **MapStruct** (DTO Mapping)
- **Lombok** (Boilerplate Reduction)
- **Keycloak** (OAuth2/JWT Authentication)
- **Testcontainers** (Integration Testing)

### Project Structure
```
src/main/java/com/synq/backend/
├── config/              # Security, CORS, OpenAPI configs
├── controller/          # REST endpoints
├── core/                # BaseEntity and shared abstractions
├── dto/
│   ├── request/         # DTOs for incoming requests
│   └── response/        # DTOs for API responses
├── enums/               # Enumerations (UserStatus, MembershipRole, MessageType)
├── exceptions/          # Custom exceptions
├── handler/             # Global exception handler
├── mapper/              # MapStruct mappers
├── model/               # JPA entities
├── repository/          # Spring Data JPA repositories
└── service/
    ├── impl/            # Service implementations
    └── [interfaces]     # Service interfaces

src/main/resources/
├── db/migration/        # Flyway SQL migrations (V1-V8)
├── application.yml
├── application-dev.yml
└── application-prod.yml
```

## 📦 Domain Model

### Entities (All extend `BaseEntity`)

1. **User** - Application users (linked to Keycloak via `keycloakExternalId`)
2. **Frequency** - Communication channels/rooms
3. **Membership** - User membership in frequencies (with roles: OWNER, MODERATOR, MEMBER, GUEST)
4. **Message** - Messages posted in frequencies (supports replies, attachments)
5. **Attachment** - File attachments linked to messages
6. **Invite** - Invite tokens for joining frequencies
7. **AuditLog** - Audit trail for actions (simplified, no BaseEntity)

### BaseEntity
All main entities inherit:
- `id` (BIGINT, auto-increment)
- `externalId` (CHAR(36), UUID, unique)
- `createdAt`, `updatedAt` (DATETIME(6))
- `version` (optimistic locking)
- `active` (soft delete flag)

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+ running (or Docker)
- Keycloak instance (optional for local dev)

### Environment Variables

Create a `.env` file or export these variables:

```bash
# Database Configuration
export SYNC_DB_URL="jdbc:mysql://localhost:3306/synq?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export SYNC_DB_USER="root"
export SYNC_DB_PASSWORD="yourpassword"

# Keycloak Configuration
export KEYCLOAK_ISSUER_URI="http://localhost:8080/realms/synq"
export KEYCLOAK_CLIENT_ID="synq-backend"

# Redis (optional, currently disabled)
export REDIS_HOST="localhost"
```

### Database Setup

#### Option 1: Let Flyway create the database
Ensure your MySQL user has `CREATE DATABASE` privileges. Flyway will execute `V202511292359__create_database.sql` to create the `synq` database.

#### Option 2: Manually create database
```sql
CREATE DATABASE synq CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Then comment out or skip `V202511292359__create_database.sql`.

### Build & Run

```bash
# Clean and compile
mvn clean compile

# Run Flyway migrations (optional - will run on startup)
mvn flyway:migrate

# Package application
mvn package -DskipTests

# Run application (dev profile)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Or run JAR
java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

The application will start on `http://localhost:8083`

### Run Tests

```bash
# Run all tests (uses Testcontainers MySQL)
mvn test

# Run specific test
mvn test -Dtest=UserServiceIntegrationTest
```

**Note:** Tests use Testcontainers and will automatically start a MySQL 8.0 container.

## 🔐 Authentication & Authorization

### Keycloak Integration

The application expects JWT tokens issued by Keycloak.

#### Token Structure Expected
```json
{
  "sub": "keycloak-user-id-uuid",
  "preferred_username": "johndoe",
  "email": "john@example.com",
  "realm_access": {
    "roles": ["user", "admin"]
  },
  "resource_access": {
    "synq-backend": {
      "roles": ["frequency_owner", "moderator"]
    }
  }
}
```

#### Role Mapping
- Realm roles: `realm_access.roles` → `ROLE_USER`, `ROLE_ADMIN`
- Client roles: `resource_access.{client-id}.roles` → `ROLE_FREQUENCY_OWNER`, `ROLE_MODERATOR`

#### User Provisioning
On first login, the `KeycloakService` provisions a `User` entity using the JWT `sub` claim as `keycloakExternalId`.

### Making Authenticated Requests

Include the Bearer token in the `Authorization` header:

```bash
curl -H "Authorization: Bearer <JWT_TOKEN>" \
     http://localhost:8083/api/frequencies
```

### WebSocket Authentication (Future)
For WebSocket connections (not yet implemented), clients can pass the token via:
- Query parameter: `?access_token=<JWT_TOKEN>`
- Or `Authorization` header during handshake

## 📡 API Endpoints

### Users (`/api/users`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | Create user |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/username/{username}` | Get user by username |
| GET | `/api/users` | Get all users |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Frequencies (`/api/frequencies`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/frequencies` | Create frequency |
| GET | `/api/frequencies` | List public frequencies |
| GET | `/api/frequencies/{id}` | Get frequency by ID |
| GET | `/api/frequencies/slug/{slug}` | Get frequency by slug |
| PUT | `/api/frequencies/{id}` | Update frequency |
| DELETE | `/api/frequencies/{id}` | Delete frequency |
| POST | `/api/frequencies/{id}/members` | Add member |
| GET | `/api/frequencies/{id}/members` | List members |
| POST | `/api/frequencies/{id}/messages` | Post message |
| GET | `/api/frequencies/{id}/messages` | Get messages (paginated) |

### Example Payloads

#### Create Frequency
```bash
POST /api/frequencies
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "General Discussion",
  "slug": "general-discussion",
  "description": "A place for general chat",
  "isPrivate": false,
  "maxParticipants": 500
}
```

#### Post Message
```bash
POST /api/frequencies/{id}/messages
Content-Type: application/json
Authorization: Bearer <token>

{
  "frequencyId": 1,
  "content": "Hello everyone!",
  "type": "TEXT"
}
```

## 🗃️ Database Migrations

Flyway migrations are in `src/main/resources/db/migration/`:

- **V1**: Create database `synq`
- **V2**: Create `users` table
- **V3**: Create `frequencies` table
- **V4**: Create `memberships` table (with unique constraint on user+frequency)
- **V5**: Create `messages` table (with reply support)
- **V6**: Create `attachments` table
- **V7**: Create `invites` table
- **V8**: Create `audit_logs` table

### Manual Flyway Commands

```bash
# Check migration status
mvn flyway:info

# Apply migrations
mvn flyway:migrate

# Clean database (CAUTION: drops all objects)
mvn flyway:clean
```

## 🧪 Testing

### Integration Tests
- `AbstractIntegrationTest`: Base class with Testcontainers MySQL setup
- `UserServiceIntegrationTest`: Tests user provisioning from Keycloak
- `MessageServiceIntegrationTest`: Tests message posting and persistence

### Running Tests
```bash
# All tests
mvn test

# With coverage
mvn test jacoco:report

# Specific test class
mvn test -Dtest=MessageServiceIntegrationTest
```

## 📊 OpenAPI / Swagger

Access interactive API documentation:
- **Swagger UI**: http://localhost:8083/v1/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8083/v1/api-docs

## 🔧 Configuration

### Profile-Specific Properties

#### Dev Profile (`application-dev.yml`)
- MySQL on `localhost:3306`
- Defaults for DB user/password
- SQL logging enabled

#### Prod Profile (`application-prod.yml`)
- Configure with production database
- Disable SQL logging
- Set appropriate Keycloak issuer URI

### Key Properties

```yaml
spring:
  datasource:
    url: ${SYNC_DB_URL}
    username: ${SYNC_DB_USER}
    password: ${SYNC_DB_PASSWORD}
  
  flyway:
    enabled: true
    baseline-on-migrate: true
  
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI}

keycloak:
  client-id: ${KEYCLOAK_CLIENT_ID}
```

## 📝 Development Notes

### SOLID Principles
- Services use constructor injection via Lombok `@RequiredArgsConstructor`
- Interfaces define contracts, implementations handle logic
- Repository methods follow naming conventions for query derivation

### Java 17 Features
- **Records** for immutable DTOs
- **Streams** for collection processing (e.g., `toList()`)
- **Text blocks** (can be used for multi-line strings)

### TODO / Future Enhancements
- [ ] Message retention policies
- [ ] File upload size validation
- [ ] Rate limiting for message posting
- [ ] WebSocket support for real-time messaging
- [ ] Redis caching for frequently accessed data
- [ ] Elasticsearch for message search
- [ ] Implement Session/Presence entities
- [ ] Implement Settings entity for user preferences

## 🐛 Troubleshooting

### Flyway Migration Fails
**Issue**: `V202511292359__create_database.sql` fails with permission error.
**Solution**: Grant `CREATE DATABASE` privilege or manually create the database and skip V1.

### JWT Validation Fails
**Issue**: `401 Unauthorized` on authenticated endpoints.
**Solution**: 
- Verify `KEYCLOAK_ISSUER_URI` matches your Keycloak realm
- Check JWT token is not expired
- Ensure client-id in JWT matches `keycloak.client-id` config

### Tests Hang or Fail
**Issue**: Testcontainers can't start MySQL.
**Solution**: 
- Ensure Docker is running
- Check Docker has sufficient resources (memory, disk)
- Run `docker pull mysql:8.0` manually

### MapStruct Mappers Not Generated
**Issue**: Compilation errors about missing mapper implementations.
**Solution**: 
- Run `mvn clean compile` to trigger annotation processors
- Verify `maven-compiler-plugin` configuration includes MapStruct processor

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [MapStruct Documentation](https://mapstruct.org/)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Testcontainers Documentation](https://www.testcontainers.org/)

## 📄 License

[Your License Here]

## 👥 Contributors

[Your Name/Team]

---

**Built with ❤️ using Spring Boot, Java 17, and modern best practices.**

