# Spring Boot REST API & Hibernate JPA Persistence

This project is a beginner-friendly Spring Boot REST API that demonstrates CRUD operations, relationship mapping, validation, and JPA persistence with MySQL.

## 1. Project Overview

The application manages departments and employees in a business database. It follows a clean layered architecture:

Controller
↓
Service
↓
Repository
↓
JPA Entity
↓
MySQL

## 2. Technologies Used

- Java 17
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- Hibernate
- MySQL
- Jakarta Validation
- Maven
- JUnit 5

## 3. Features

- REST API for departments and employees
- CRUD operations
- One-to-Many and Many-to-One relationships
- Validation with Jakarta Bean Validation
- Custom repository queries using `@Query`
- Global exception handling
- JUnit 5 testing

## 4. Architecture

The application follows a standard Spring Boot layered design:

- Controller layer: handles HTTP requests and responses
- Service layer: contains business logic
- Repository layer: uses `JpaRepository` and custom queries
- Entity layer: Hibernate/JPA entities mapped to database tables

## 5. Entity Relationships

- `Department` has many `Employee` records
- `Employee` belongs to one `Department`
- Relationship type: One-to-Many (Department -> Employee) and Many-to-One (Employee -> Department)

## 6. API Endpoints

### Employees

- `GET /api/employees` - Get all employees
- `GET /api/employees/{id}` - Get employee by ID
- `POST /api/employees` - Create employee
- `PUT /api/employees/{id}` - Update employee
- `DELETE /api/employees/{id}` - Delete employee
- `GET /api/employees/role/{role}` - Get employees by role
- `GET /api/employees/department/{departmentName}` - Get employees by department name
- `GET /api/employees/salary-above/{salary}` - Get employees with salary above threshold

### Departments

- `GET /api/departments` - Get all departments
- `GET /api/departments/{id}` - Get department by ID
- `POST /api/departments` - Create department
- `PUT /api/departments/{id}` - Update department
- `DELETE /api/departments/{id}` - Delete department

## 7. Validation

The API uses Jakarta Validation annotations such as:

- `@NotNull`
- `@NotBlank`
- `@Size`
- `@Email`
- `@Positive`

These rules are applied in DTOs and enforced with `@Valid` in the controllers.

## 8. Exception Handling

The application uses a global exception handler to return clean JSON responses for:

- `ResourceNotFoundException` with HTTP 404
- `MethodArgumentNotValidException` with HTTP 400

## 9. Database Configuration

This project uses MySQL. The database name is:

- `rabtech_business_db`

Configuration is in `src/main/resources/application.properties`.

## 10. How to Run

1. Make sure MySQL is installed and running.
2. Create a database named `rabtech_business_db`.
3. Update the database password in `application.properties`.
4. Run the application:

```bash
mvn spring-boot:run
```

## 11. Maven Commands

```bash
mvn clean test
mvn clean package
mvn spring-boot:run
```

## 12. Sample API Requests

### Create Department

```http
POST /api/departments
Content-Type: application/json

{
  "name": "Engineering"
}
```

### Create Employee

```http
POST /api/employees
Content-Type: application/json

{
  "name": "Priyanga",
  "email": "priyanga@example.com",
  "role": "Developer",
  "salary": 50000.0,
  "departmentId": 1
}
```

### Get Employee by ID

```http
GET /api/employees/1
```

## 13. Testing

The project includes JUnit 5 tests that verify:

- application context loads
- CRUD endpoints
- validation failure
- not found handling
- repository query behavior
- department relationship mapping

Tests use an H2 in-memory database profile during test execution while production keeps MySQL configuration.

## 14. Expected Results

- Application starts successfully with MySQL running
- Tables are created or updated automatically by Hibernate
- REST API endpoints return JSON responses
- Validation enforces required data rules
- Exceptions return structured error responses
