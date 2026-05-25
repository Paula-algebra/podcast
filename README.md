# PodCast

A personal podcast episode tracker and listening journal built as a Spring Boot 4 project with an MVC interface and a REST API.

## Technologies

| Component | Version |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.6 |
| Spring MVC / Spring Security | 7.x |
| Thymeleaf | 3.x |
| Spring Data JPA / Hibernate | 7.x |
| H2 (in-memory) | runtime |
| Auth0 Java JWT | 4.4.0 |
| springdoc-openapi | 3.0.0 |

## Running the Project in IntelliJ IDEA

1. **Open the project:** `File → Open` → select the `podcast` folder
2. **SDK:** `File → Project Structure → SDK` → set to **Java 25**
3. **Maven:** IntelliJ will automatically download dependencies; if not, run **Reload Maven Project**
4. **Run:** `PodcastApplication.java` → right-click → *Run*
5. **Access:** [http://localhost:8080](http://localhost:8080)

## Default User Accounts (in-memory H2)

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | ADMIN — full management |
| `user` | `user123` | USER — read and search only |

## MVC Interface

| URL | Description | Access |
|---|---|---|
| `/` | Redirect to `/episodes` | — |
| `/auth/login` | Login form | Public |
| `/auth/register` | Registration form | Public |
| `/episodes` | Browse and search episodes | USER + ADMIN |
| `/episodes/{id}` | Episode details with full breakdown | USER + ADMIN |
| `/episodes/new` | Add new episode form | ADMIN |
| `/episodes/edit/{id}` | Edit episode form | ADMIN |
| `/episodes/delete/{id}` | Delete episode (POST) | ADMIN |

## REST API

Base URL: `/api`

### Authentication

| Method | URL | Description |
|---|---|---|
| `POST` | `/api/auth/login` | Login — returns access + refresh token |
| `POST` | `/api/auth/register` | Register a new user account |
| `POST` | `/api/auth/refresh` | Obtain a new access token |
| `POST` | `/api/auth/logout` | Revoke the refresh token |

### Episodes (requires Bearer token)

| Method | URL | Description | Role |
|---|---|---|---|
| `GET` | `/api/episodes` | All episodes | USER + ADMIN |
| `GET` | `/api/episodes/{id}` | Single episode | USER + ADMIN |
| `GET` | `/api/episodes/search` | Search (`query`, `category`, `status`, `subscribedOnly`) | USER + ADMIN |
| `POST` | `/api/episodes` | Add new episode | ADMIN |
| `PUT` | `/api/episodes/{id}` | Update episode | ADMIN |
| `DELETE` | `/api/episodes/{id}` | Delete episode | ADMIN |

### Example: Login and API Usage

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Fetch episodes using the access token
curl http://localhost:8080/api/episodes \
  -H "Authorization: Bearer <access_token>"
```

## Swagger UI

Available at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

1. Call `POST /api/auth/login` with `admin` / `admin123`
2. Copy the `accessToken` from the response
3. Click **Authorize** (top right) → paste the token
4. Use any of the secured endpoints

## H2 Console

URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:podcastdb` |
| Username | `sa` |
| Password | *(leave blank)* |

## Key Features

- **Animated audio waveform background** — login page features 15 animated CSS-only waveform bars that pulse like a real audio visualizer
- **"Now Playing" pulsing indicator** — episodes with LISTENING status get a glowing gradient badge with an animated pulse dot in the corner of the card (the most visually distinctive status indicator yet!)
- **Live listening progress bar with glowing scrubber dot** — for in-progress episodes, a beautiful audio-player-style progress bar shows minutes listened vs total duration, complete with a white scrubber dot with glow effect at the end of the played portion
- **Full audio player hero card** — detail view features a coral gradient player card with massive percentage, scrubber dot, and monospace timestamp (e.g. "45:00 / 92:00")
- **Playback speed badges in monospace** — display playback speeds as "1.25X" / "1.5X" / "2.0X" in teal monospace font — the iconic podcast listener flex
- **5 listening statuses** — QUEUED, LISTENING, FINISHED, SKIPPED, SAVED_FOR_LATER — each with unique status stripe colors
- **8 listening contexts** — track WHERE you listened: COMMUTE, WORKOUT, HOUSEWORK, WALKING, COOKING, SLEEP, FOCUSED_LISTEN, BACKGROUND
- **Subscribed star indicator** — gold star icon next to the show name on cards you've subscribed to
- **Subscribed-only filter** — dedicated checkbox in the search bar to filter only shows you follow
- **Four-dimensional quality scoring** — Content, Audio, Host Chemistry, Re-listen value (1-10 each) with animated gradient bars
- **Key takeaway field** — separate dedicated field for the main insight, displayed in a gold-bordered emphasis box on the detail view
- **15 podcast categories** including True Crime, Comedy, Tech, Mental Health, Pop Culture
- **"▶ Press Play" custom CTA** — themed login button with play triangle icon

## Project Structure

```
src/main/java/hr/algebra/podcast/
├── PodcastApplication.java
├── config/
│   ├── DataInitializer.java          # 10 sample episodes on startup
│   ├── OpenApiConfig.java            # Swagger / OpenAPI configuration
│   └── SecurityConfig.java           # Two filter chains (API + MVC)
├── controller/
│   ├── mvc/
│   │   ├── AuthMvcController.java
│   │   ├── EpisodeMvcController.java
│   │   └── HomeController.java
│   └── rest/
│       ├── AuthRestController.java
│       └── EpisodeRestController.java
├── dto/
│   ├── Dto.java                      # Login/Register/Token records
│   └── EpisodeDto.java               # Java record
├── entity/
│   ├── Episode.java
│   ├── RefreshToken.java
│   └── User.java                     # Implements UserDetails
├── enums/
│   ├── ListeningContext.java
│   ├── ListeningStatus.java
│   ├── PlaybackSpeed.java
│   ├── PodcastCategory.java
│   └── Role.java
├── repository/
│   ├── EpisodeRepository.java
│   ├── RefreshTokenRepository.java
│   └── UserRepository.java
├── security/
│   ├── JwtAuthFilter.java
│   ├── JwtService.java
│   └── UserDetailsServiceImpl.java
└── service/
    ├── AuthService.java
    ├── EpisodeService.java
    └── RefreshTokenService.java
```
