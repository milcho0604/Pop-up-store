# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.6 application for managing popup store information. Users can create posts about popup stores, submit information reports that admins can approve, and interact with posts through views and likes. The application uses JWT authentication, Redis for metrics caching, AWS S3 for file storage, and MariaDB for persistence.

## Build & Development Commands

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Run tests
./gradlew test

# Clean build artifacts
./gradlew clean

# Build without tests
./gradlew build -x test
```

## Architecture

### Module Structure

The codebase follows a domain-driven design with the following main modules:

- **pop** - Core popup store posts (user-created content)
- **information** - Popup store information reports (user submissions pending admin approval)
- **member** - User authentication and profile management
- **report** - User reporting system for posts
- **common** - Shared utilities, configs, DTOs, and base entities
- **healthCheck** - Service health monitoring

### Key Architectural Patterns

**Domain Layer Organization:**
Each domain module follows a standard structure:
```
domain/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # JPA repositories
├── domain/         # JPA entities
├── dto/            # Data transfer objects
├── mapper/         # MapStruct mappers (where applicable)
└── policy/         # Domain-specific validation logic
```

**Information → Post Flow:**
Information submissions are user-reported popup stores pending admin approval. When an admin approves an Information entity, it gets converted to a Post entity via `Post.convertFromInformation()` (Post.java:144). The Information entity is then soft-deleted (deletedAt set) but remains in the database.

**Soft Delete Pattern:**
All entities extend `BaseTimeEntity` which provides `createdAt`, `updatedAt`, and `deletedAt` timestamps. Soft deletes are implemented by setting `deletedAt` to the current time. Queries filter by `deletedAt IS NULL` to exclude deleted records.

**Metrics with Redis:**
Post views and likes are stored in Redis (database 7) rather than the main database for performance:
- Views: `post:views:{postId}` (incremented per view)
- Likes: `post:likes:{postId}` (Set of user emails)
- View deduplication: `post:views:users:{postId}` (Set of user emails who viewed)

These metrics are managed by `PostMetricsService` and periodically synchronized to the database by `PostBatchService` (currently disabled with `@EnableScheduling` commented out in PopupApplication.java).

**MapStruct Integration:**
The project uses MapStruct for DTO-entity mapping with Lombok. The annotation processor order is configured in build.gradle:38-43 to prevent conflicts. See `PostMapper` for the pattern.

### Security & Authentication

**JWT-based Authentication:**
- JwtTokenProvider creates tokens with email, role, and memberId claims
- JwtAuthFilter intercepts requests and validates Bearer tokens
- SecurityConfig permits all endpoints (`/**`) but JWT filter still processes tokens
- Member roles defined in `common.enumdir.Role`

**Two Redis Configurations:**
- Primary (DB 0): Email verification codes via `javaRedisTemplate`
- Database 7: Post metrics via `redisTemplateDb7`

### File Uploads

**AWS S3 Integration:**
- S3ClientFileUpload handles all file uploads
- Files are prefixed with UUID to prevent collisions
- Returns public S3 URL: `https://{bucket}.s3.amazonaws.com/{filename}`
- Used for post images (`postImgUrl`) and member profile images (`profileImgUrl`)

### Configuration

**Application Profiles:**
- Default profile: `local` (set in application.yml)
- Local configuration in `application-local.yml` includes:
  - MariaDB on localhost:3307
  - Redis on localhost:6379
  - JWT secret keys
  - AWS S3 credentials
  - Email configuration (Gmail SMTP)

## Common Development Patterns

**BaseTimeEntity Touch Method:**
Call `entity.touch()` to force update the `updatedAt` timestamp when making changes that don't modify fields directly (BaseTimeEntity.java:39-41).

**Partial Updates:**
Domain entities implement partial update methods that only modify non-null fields. See `Post.update()` (Post.java:93-130) and `Information.update()` for the pattern.

**Address Embedding:**
Address is a `@Embedded` value object with city, street, zipcode, and detailAddress fields. It's immutable - updates require rebuilding the entire Address object.

**Redis Metrics Retrieval:**
Always handle Redis value type casting for metrics (Integer/Long/String) as shown in PostMetricsService.java:42-56 to prevent ClassCastException.

## Database

- **Primary DB:** MariaDB with JPA/Hibernate
- **DDL Strategy:** `update` (auto-creates/updates schema)
- **SQL Logging:** Enabled with formatting in local profile
- **Dialect:** MariaDBDialect

## Testing

Tests are minimal - only the default `PopupApplicationTests` exists. The test suite uses JUnit Platform Launcher.

## Important Notes

- Scheduling is currently disabled (`@EnableScheduling` commented out in PopupApplication.java)
- All REST endpoints are publicly accessible per SecurityConfig, though JWT is still validated when present
- Redis database 7 is dedicated to post metrics; don't use it for other purposes
- The Information approval workflow sets `deletedAt` on the Information entity after conversion to Post
