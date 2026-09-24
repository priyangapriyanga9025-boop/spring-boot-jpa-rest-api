# Workforce Command Center

Enterprise Java Full Stack Capstone: a responsive Employee and Department Management Portal backed by Spring Boot, MySQL, JWT authentication, and documented REST APIs.

## Overview

Workforce Command Center gives authenticated users one place to manage departments and employees. The browser frontend is served by Spring Boot and communicates with the real REST API using the Fetch API. No mock records or fake API responses are used.

## Features

- User registration and JWT login with BCrypt password hashing
- Stateless authenticated sessions stored in browser `sessionStorage`
- Logout that clears the current browser session
- Responsive HTML5/CSS3/JavaScript dashboard
- Live employee and department totals from MySQL
- Department create, read, update, and delete workflows
- Employee create, read, update, and delete workflows
- Employee search by text and department filter
- Loading, empty, success, validation, not-found, and unauthorized states
- Structured global JSON exception responses
- Springdoc OpenAPI documentation and bearer authorization
- Automated MockMvc tests with an H2 test database

## Technology Stack

- Java 17
- Spring Boot 3.3.4
- Spring Web and Spring Security
- Spring Data JPA and Hibernate
- MySQL for local/runtime persistence
- JJWT 0.12.6
- Springdoc OpenAPI 2.6.0
- Maven
- HTML5, CSS3, and browser JavaScript Fetch API
- JUnit 5, MockMvc, Spring Security Test, and H2

## Architecture

```text
Browser frontend
       |
       v
JWT-authenticated REST API
       |
       v
Controller -> Service -> Repository -> JPA Entity -> MySQL
```

The frontend is served from `src/main/resources/static`. Controllers validate request DTOs and delegate business operations to services. Repositories persist `User`, `Department`, and `Employee` entities.

## Project Structure

```text
src/main/java/com/rabtech/api
├── config       OpenAPI configuration
├── controller   REST endpoints
├── dto          Validated request and response models
├── entity       User, Department, and Employee JPA entities
├── exception    Resource exception and global REST advice
├── repository   Spring Data repositories
├── security     JWT service, filter, and Spring Security config
└── service      Authentication and business logic
src/main/resources
├── application.properties
└── static
    ├── index.html
    ├── css/styles.css
    └── js/app.js
```

## Database Setup

Create the database in MySQL Workbench:

```sql
CREATE DATABASE rabtech_business_db;
```

The runtime configuration connects to MySQL on `localhost:3306` as `root`. Hibernate uses `ddl-auto=update` for this local project and creates or updates the tables. Use a dedicated database user and migrations before production deployment.

## Configuration

Set these environment variables in the PowerShell terminal used to start the application:

```powershell
$env:DB_PASSWORD = "your_mysql_password"
$env:JWT_SECRET = "your_long_random_jwt_secret"
$env:JWT_EXPIRATION = "86400000"
```

Do not commit passwords, JWT values, or secret files. The repository ignores `.env`, `*.env`, and `application-local.properties`.

## Run the Backend and Frontend

The frontend is bundled into the Spring Boot application, so no separate frontend server is required.

```powershell
mvn clean test
mvn spring-boot:run
```

Open the portal at `http://localhost:8080/`.

Because environment variables are scoped to a PowerShell process, set `DB_PASSWORD` again when opening a new terminal before running the application.

## Authentication Flow

1. Register with `POST /api/auth/register` using role `USER` or `ADMIN`.
2. Log in with `POST /api/auth/login`.
3. The frontend stores the returned token only in `sessionStorage` for the current browser session.
4. Protected requests send exactly `Authorization: Bearer <JWT>`.
5. A `401 Unauthorized` response clears the session and returns the user to the login view.
6. Logout clears all stored session values.

The API is stateless; logout does not need a server-side token revocation call.

## REST API

### Authentication

- `POST /api/auth/register`
- `POST /api/auth/login`

### Departments

- `GET /api/departments`
- `GET /api/departments/{id}`
- `POST /api/departments`
- `PUT /api/departments/{id}`
- `DELETE /api/departments/{id}`

### Employees

- `GET /api/employees`
- `GET /api/employees/{id}`
- `POST /api/employees`
- `PUT /api/employees/{id}`
- `DELETE /api/employees/{id}`
- `GET /api/employees/role/{role}`
- `GET /api/employees/department/{departmentName}`
- `GET /api/employees/salary-above/{salary}`

## Swagger / OpenAPI

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

To authorize Swagger:

1. Log in and copy the JWT value only.
2. Select **Authorize** in Swagger UI.
3. Paste only the raw JWT, without the word `Bearer`.
4. Select **Authorize**, then close the dialog.

Swagger adds the prefix and sends exactly `Authorization: Bearer <JWT>`. Entering `Bearer <JWT>` manually would create an invalid double prefix.

## Global Exception Handling

`GlobalExceptionHandler` uses `@RestControllerAdvice` and `@ExceptionHandler` for:

- `ResourceNotFoundException`: HTTP 404
- `MethodArgumentNotValidException`: HTTP 400 with field details
- `BadCredentialsException`: HTTP 401
- `IllegalArgumentException`: HTTP 409

Responses include `timestamp`, `status`, `error`, `message`, and `path`.

## Testing

Run the complete automated suite:

```powershell
mvn test
```

The tests cover registration, encrypted passwords, login, invalid credentials, protected routes, JWT validation, CRUD operations, validation, entity relationships, repository queries, and OpenAPI exposure. Tests use the H2 profile and do not require the MySQL password.

Build the packaged application:

```powershell
mvn clean package
```

## Sample API Usage

Register without committing real credentials:

```powershell
$body = @{ username = "demo_user"; password = "ChangeThisPassword123!"; role = "USER" } | ConvertTo-Json
Invoke-RestMethod http://localhost:8080/api/auth/register -Method Post -ContentType 'application/json' -Body $body
```

After login, use the returned token in memory:

```powershell
Invoke-WebRequest http://localhost:8080/api/departments -Headers @{ Authorization = "Bearer <JWT>" }
```

## Screenshots and Demo

For the capstone submission, capture:

- Login or registration screen
- Authenticated dashboard with live employee and department data
- Employee edit and search workflow
- Department management workflow
- Swagger UI authorized with a raw JWT

A short walkthrough should show registration, login, dashboard loading, department and employee CRUD, logout, Swagger authorization, and the structured 404 response for `/api/departments/9999`.

## GitHub Readiness

The project contains the Maven source, tests, static frontend, Postman collection, and this setup documentation. Secrets are supplied through environment variables and are excluded by `.gitignore`. Push to GitHub only after adding screenshots and reviewing the repository for local-only files.
