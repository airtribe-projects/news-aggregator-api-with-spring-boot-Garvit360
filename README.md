# NewsAggregator

A Spring Boot application for aggregating news articles and managing user preferences.

## Features
- User registration and JWT-based authentication
- Manage news preferences (get/update)
- Fetch news from GNews API
- Search news by keyword
- Caching with Caffeine
- In-memory H2 database

## Prerequisites
- Java 17
- Gradle or Maven
- GNews API key (set `gnews.key` in `application.properties`)

## Running the Application
```bash
./gradlew bootRun
# or
./gradlew build && java -jar build/libs/news-aggregator-0.0.1-SNAPSHOT.jar
```

## API Endpoints
- POST /api/register
- POST /api/login
- GET /api/preferences
- PUT /api/preferences
- GET /api/news
- GET /api/news/search/{keyword}

## Configuration
Configure `src/main/resources/application.properties` with your GNews API key and other settings.

## License
MIT 