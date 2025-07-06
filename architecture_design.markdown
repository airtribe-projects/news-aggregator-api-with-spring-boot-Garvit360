# Architecture Design for News Aggregation Platform

## 1. Overview
This document outlines the architecture design for a Spring Boot-based news aggregation platform. The application provides user authentication, personalized news preferences, and integration with external news APIs. It includes RESTful APIs for user management, news preferences, and news fetching, with optional extensions for caching, marking articles, and keyword-based search.

## 2. System Architecture
The application follows a **layered architecture** to ensure modularity, maintainability, and scalability. The layers are:

1. **Presentation Layer**: Handles HTTP requests and responses via RESTful APIs.
2. **Service Layer**: Contains business logic for user management, authentication, and news processing.
3. **Data Access Layer**: Manages database operations using Spring Data JPA with an in-memory database (H2).
4. **Integration Layer**: Interacts with external news APIs using Spring WebClient.
5. **Security Layer**: Implements JWT-based authentication and authorization using Spring Security.
6. **Optional Caching Layer**: Uses Spring Cache (e.g., Caffeine or Redis) for caching news articles.

### 2.1. Technology Stack
- **Framework**: Spring Boot 3.x
- **Dependencies**:
  - Spring Web (REST APIs)
  - Spring Security (JWT authentication)
  - Spring Data JPA (ORM for database operations)
  - H2 Database (in-memory database)
  - Spring WebClient (for external API calls)
  - JJWT (JWT generation and validation)
  - Lombok (to reduce boilerplate code)
  - Spring Boot Starter Validation (for input validation)
  - Spring Cache (optional, for caching)
- **External APIs**:
  - NewsAPI (100 requests/day)
  - NewsCatcher News API
  - GNews API (100 requests/day)
  - NewsAPI.ai (2000 requests/month)
- **Testing Tools**: Postman, Curl
- **Build Tool**: Maven

## 3. System Components

### 3.1. Presentation Layer
The presentation layer exposes RESTful endpoints using Spring MVC. The endpoints are:

| Endpoint                     | Method | Description                                      | Authentication |
|------------------------------|--------|--------------------------------------------------|----------------|
| `/api/register`              | POST   | Register a new user                              | None           |
| `/api/login`                 | POST   | Authenticate a user and return a JWT            | None           |
| `/api/preferences`           | GET    | Retrieve user news preferences                   | JWT            |
| `/api/preferences`           | PUT    | Update user news preferences                    | JWT            |
| `/api/news`                  | GET    | Fetch news articles based on preferences         | JWT            |
| `/api/news/{id}/read`        | POST   | Mark an article as read (optional)              | JWT            |
| `/api/news/{id}/favorite`    | POST   | Mark an article as favorite (optional)          | JWT            |
| `/api/news/read`             | GET    | Retrieve read articles (optional)               | JWT            |
| `/api/news/favorites`        | GET    | Retrieve favorite articles (optional)           | JWT            |
| `/api/news/search/{keyword}` | GET    | Search news articles by keyword (optional)      | JWT            |

#### Input Validation
- Use `@Valid` and Hibernate Validator annotations (`@NotBlank`, `@Email`, `@Size`, etc.) for request bodies.
- Example: User registration requires a valid email, password (min 8 characters), and non-empty username.

### 3.2. Service Layer
The service layer encapsulates business logic and coordinates between controllers and repositories. Key services include:

- **UserService**:
  - Handles user registration and password encoding.
  - Manages news preferences (CRUD operations).
- **AuthenticationService**:
  - Generates and validates JWT tokens.
  - Authenticates users using Spring Security.
- **NewsService**:
  - Fetches news from external APIs based on user preferences.
  - Aggregates results from multiple APIs.
  - (Optional) Manages read/favorite articles and keyword-based search.
- **CacheService** (optional):
  - Manages caching of news articles using Spring Cache.
  - Implements background refresh for cached articles.

### 3.3. Data Access Layer
- **Database**: H2 in-memory database for simplicity and fast setup.
- **Entities**:
  - **User**: Stores user details (id, username, email, password, preferences).
  - **NewsArticle** (optional): Stores read/favorite articles (id, userId, articleId, status).
- **Repositories**:
  - `UserRepository`: CRUD operations for users.
  - `NewsArticleRepository` (optional): Manages read/favorite articles.

### 3.4. Integration Layer
- Uses **Spring WebClient** for asynchronous HTTP requests to external news APIs.
- Implements a **NewsApiClient** interface with concrete implementations for each API (e.g., `NewsApiClient`, `GNewsApiClient`).
- Aggregates results from multiple APIs to ensure diversity in news sources.
- Handles API rate limits by:
  - Configuring retry mechanisms for failed requests.
  - Logging rate limit errors for monitoring.

### 3.5. Security Layer
- **Spring Security Configuration**:
  - Configures JWT-based authentication using a custom `JwtAuthenticationFilter`.
  - Uses `BCryptPasswordEncoder` for password hashing.
  - Secures endpoints with `@PreAuthorize` annotations or method-level security.
- **JWT Workflow**:
  - On login, generate a JWT with user details (e.g., username, roles).
  - Validate JWT on subsequent requests using a `JwtTokenProvider`.
  - Store JWT in HTTP Authorization header (`Bearer <token>`).
- **Roles**:
  - `ROLE_USER`: Default role for registered users.
  - (Optional) `ROLE_ADMIN` for future extensions.

### 3.6. Caching Layer (Optional)
- Uses **Spring Cache** with Caffeine or Redis to cache news articles.
- Cache keys based on user preferences and API source.
- Implements a **scheduled task** (using `@Scheduled`) to refresh cached articles periodically (e.g., every 30burthday: 30 minutes).
- Evicts cache entries when preferences are updated.

## 4. Data Model
### 4.1. User Entity
```java
@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    @ElementCollection
    private List<String> preferences; // e.g., ["technology", "sports"]
}
```

### 4.2. NewsArticle Entity (Optional)
```java
@Entity
@Data
public class NewsArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String articleId; // Unique ID from external API
    private String status; // "read" or "favorite"
}
```

## 5. API Workflow
### 5.1. User Registration (`POST /api/register`)
1. Validate request body (username, email, password).
2. Check for duplicate email/username.
3. Encode password using `BCryptPasswordEncoder`.
4. Save user to `UserRepository`.
5. Return success response.

### 5.2. User Login (`POST /api/login`)
1. Validate credentials using `AuthenticationManager`.
2. Generate JWT with user details and expiration.
3. Return JWT in response.

### 5.3. Get Preferences (`GET /api/preferences`)
1. Extract user from JWT.
2. Retrieve preferences from `UserRepository`.
3. Return preferences list.

### 5.4. Update Preferences (`PUT /api/preferences`)
1. Validate preferences list.
2. Extract user from JWT.
3. Update preferences in `UserRepository`.
4. Evict user-specific cache entries.
5. Return updated preferences.

### 5.5. Fetch News (`GET /api/news`)
1. Extract user from JWT.
2. Retrieve user preferences.
3. Query external APIs concurrently using WebClient.
4. Aggregate results and filter by preferences.
5. (Optional) Cache results with user-specific key.
6. Return news articles.

### 5.6. Optional Endpoints
- **Mark Read/Favorite**: Save article status to `NewsArticleRepository`.
- **Get Read/Favorites**: Query `NewsArticleRepository` by userId and status.
- **Search by Keyword**: Query external APIs with keyword parameter and return results.

## 6. Exception Handling
- **Global Exception Handler** (`@ControllerAdvice`):
  - `MethodArgumentNotValidException`: Return 400 with validation errors.
  - `AuthenticationException`: Return 401 with "Invalid credentials".
  - `AccessDeniedException`: Return 403 with "Unauthorized access".
  - `HttpClientErrorException`: Handle API rate limits or errors (429, 500, etc.).
  - Generic `Exception`: Return 500 with "Internal server error".
- Custom exceptions for business logic (e.g., `UserAlreadyExistsException`).

## 7. External API Integration
- **NewsApiClient Interface**:
```java
public interface NewsApiClient {
    CompletableFuture<List<NewsArticleDTO>> fetchNews(String preferences, String keyword);
}
```
- Each API client handles:
  - API key configuration via `application.properties`.
  - Rate limit handling with retries (using `RetryTemplate`).
  - Mapping API-specific responses to a unified `NewsArticleDTO`.

## 8. Caching (Optional)
- Use `@Cacheable` on `NewsService` methods to cache news articles.
- Cache configuration:
```java
@Bean
public CacheManager cacheManager() {
    return new CaffeineCacheManager("news");
}
```
- Scheduled task for cache refresh:
```java
@Scheduled(fixedRate = 30 * 60 * 1000) // Every 30 minutes
public void refreshNewsCache() {
    // Fetch and cache news for all users
}
```

## 9. Testing
- Use **Postman** or **Curl** to test endpoints.
- Test cases:
  - Register with valid/invalid data.
  - Login with correct/incorrect credentials.
  - Fetch/update preferences with valid JWT.
  - Fetch news with/without preferences.
  - (Optional) Test read/favorite endpoints and keyword search.
- Verify HTTP status codes and response payloads.

## 10. Project Structure
```
src/main/java/com/example/newsapp
├── config
│   ├── SecurityConfig.java
│   ├── WebClientConfig.java
│   └── CacheConfig.java (optional)
├── controller
│   ├── AuthController.java
│   ├── PreferencesController.java
│   └── NewsController.java
├── service
│   ├── UserService.java
│   ├── AuthenticationService.java
│   ├── NewsService.java
│   └── NewsApiClient.java (interface)
├── client
│   ├── NewsApiClientImpl.java
│   ├── GNewsApiClient.java
│   └── NewsCatcherApiClient.java
├── repository
│   ├── UserRepository.java
│   └── NewsArticleRepository.java (optional)
├── model
│   ├── User.java
│   ├── NewsArticle.java (optional)
│   └── dto
│       ├── UserDTO.java
│       ├── LoginDTO.java
│       └── NewsArticleDTO.java
├── security
│   ├── JwtTokenProvider.java
│   └── JwtAuthenticationFilter.java
└── exception
    ├── GlobalExceptionHandler.java
    └── CustomException.java
```

## 11. Configuration
- **application.properties**:
```properties
spring.datasource.url=jdbc:h2:mem:newsdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
jwt.secret=your_jwt_secret
jwt.expiration=86400000
newsapi.key=your_newsapi_key
gnews.key=your_gnews_key
newscatcher.key=your_newscatcher_key
```

## 12. Future Considerations
- **Scalability**:
  - Replace H2 with PostgreSQL/MySQL for production.
  - Use Redis for distributed caching.
- **Security**:
  - Implement refresh tokens for JWT.
  - Add rate limiting for API endpoints.
- **Monitoring**:
  - Integrate logging with SLF4J/Logback.
  - Add metrics with Spring Actuator.

This architecture ensures a robust, secure, and scalable news aggregation platform with clear separation of concerns and extensibility for optional features.