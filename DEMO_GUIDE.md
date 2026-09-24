# Workforce Command Center Demo Guide

## Purpose

This guide is a simple 5-7 minute walkthrough for presenting the Workforce Command Center capstone. Use a temporary demo account and never show a real password, JWT, database credential, or secret on screen.

## A. Project Introduction

**What to click/show**

Open `http://localhost:8080/` and show the Workforce Command Center login screen.

**What to say**

"This is my Enterprise Java Full Stack Capstone Project, Workforce Command Center. It is an Employee and Department Management Portal with a Spring Boot REST backend and a responsive HTML5, CSS3, and JavaScript frontend."

**Expected result**

The login page loads with the application branding and Sign in/Create account options.

## B. Technologies Used

**What to click/show**

Show the project in VS Code and briefly point to `pom.xml`, the Java source tree, and `src/main/resources/static`.

**What to say**

"The project uses Java 17, Spring Boot, Spring Data JPA, Hibernate, MySQL, Spring Security, JWT authentication, Maven, Springdoc OpenAPI, and a browser frontend built with HTML5, CSS3, and JavaScript."

**Expected result**

The audience can see that the project contains both backend and frontend source code.

## C. Application Architecture

**What to click/show**

Show the package folders `controller`, `service`, `repository`, `entity`, `security`, `exception`, and the static frontend folder.

**What to say**

"The browser frontend calls the REST API. Requests move through the Controller, Service, and Repository layers, and JPA persists the data in MySQL. Spring Security validates the JWT before protected requests reach the controllers."

**Expected result**

The layered architecture is clear and matches the source tree.

## D. Login and JWT Demonstration

**What to click/show**

1. Select **Create account**.
2. Register a temporary user with role `USER`.
3. Select **Sign in**.
4. Log in with that temporary user.
5. Open browser developer tools only if needed, and show that the session uses `sessionStorage` without revealing the token value.

**What to say**

"Registration uses the real backend authentication API. After login, the backend returns a JWT. The frontend stores it only for this browser session and uses it for protected API calls."

**Expected result**

The dashboard opens and live employee and department totals load from the database.

## E. Employee CRUD Demonstration

**What to click/show**

1. Select **+ Add employee**.
2. Enter a name, email, role, salary, and an existing department.
3. Save the employee.
4. Select **Edit** for the new employee and change the role or salary.
5. Save the update.
6. Select **Delete** and confirm.

**What to say**

"These are real create, update, and delete operations against the Employee REST API. The table refreshes after each operation, so the dashboard always shows current database data."

**Expected result**

The employee appears, updates successfully, and is removed after deletion.

## F. Department CRUD Demonstration

**What to click/show**

1. Select **+ Add department**.
2. Enter a temporary department name.
3. Save it.
4. Select **Edit**, change the name, and save.
5. Delete the department after removing any employees assigned to it.

**What to say**

"Departments use the same real REST integration. The portal supports department creation, editing, listing, and deletion."

**Expected result**

The department list and department count update after each successful operation.

## G. Search and Filter Demonstration

**What to click/show**

1. Type an employee name, email, or role in the employee search box.
2. Select a department from the department filter.
3. Select **Clear**.

**What to say**

"The employee directory supports quick text search and department filtering. These controls help a user find records without leaving the dashboard."

**Expected result**

Only matching employees are displayed, and Clear restores the complete list.

## H. Global Exception Handling Demonstration

**What to click/show**

Open Swagger UI at `http://localhost:8080/swagger-ui.html`, authorize with a real JWT, and execute `GET /api/departments/9999`.

**What to say**

"This request asks for a department that does not exist. The global exception handler catches the resource-not-found exception and returns a clean JSON 404 response instead of an unhandled error."

**Expected result**

The response is HTTP `404 Not Found` with a meaningful message such as `Department not found with id: 9999`, plus status and request path details.

## I. Swagger and OpenAPI Demonstration

**What to click/show**

1. Open `http://localhost:8080/swagger-ui.html`.
2. Select **Authorize**.
3. Paste only the raw JWT value, without typing `Bearer`.
4. Close the dialog.
5. Open `GET /api/departments`.
6. Select **Try it out**, then **Execute**.
7. Show the generated curl or request details.

**What to say**

"Springdoc documents the REST endpoints. Swagger UI is configured with an HTTP bearer scheme, so I enter only the raw token. Swagger adds the prefix exactly once."

**Expected result**

The outgoing header is:

```text
Authorization: Bearer <JWT>
```

It must never be:

```text
Authorization: Bearer Bearer <JWT>
```

The protected request returns a successful response.

## J. Logout and Security Demonstration

**What to click/show**

1. Return to the dashboard and select **Log out**.
2. Confirm that the login page is shown.
3. If demonstrating invalid-token recovery, place an invalid value in the current session storage only, reload the page, and show that the app returns to login. Do not show any real token.

**What to say**

"Logout clears the browser session. Protected requests without a valid JWT are rejected, and an expired or invalid session returns the user to the login screen."

**Expected result**

The dashboard disappears, session storage is cleared, and unauthorized access returns HTTP `401`.

## K. Final GitHub and README Presentation

**What to click/show**

Show the GitHub repository or local README sections for features, architecture, setup, authentication, API documentation, testing, and demo instructions. Do not show secret files or terminal lines containing passwords.

**What to say**

"The repository contains the backend, frontend, tests, Postman collection, OpenAPI configuration, and complete setup documentation. Secrets are supplied through environment variables and are excluded from Git."

**Expected result**

The project looks organized, documented, and ready for review.

## L. Conclusion

**What to click/show**

Return to the dashboard or Swagger API list and leave the application on a clean, useful screen.

**What to say**

"This project demonstrates a complete full-stack workflow: secure JWT login, a responsive frontend, database-backed employee and department management, validation, global exception handling, automated tests, and documented REST APIs."

**Expected result**

The presentation ends with the working application visible.

## SCREEN RECORDING ORDER

1. Start the application with `mvn spring-boot:run` and open `http://localhost:8080/`.
2. Introduce the project from the login page.
3. Briefly show the VS Code architecture and technology stack.
4. Register a temporary `USER` account.
5. Log in and show the live dashboard counts.
6. Add, edit, search, and delete an employee.
7. Add, edit, and delete a department.
8. Demonstrate the department filter and Clear button.
9. Open Swagger UI and authorize using only the raw JWT.
10. Execute `GET /api/departments` and show the single `Bearer` prefix.
11. Execute `GET /api/departments/9999` and show the structured 404 response.
12. Log out and show that the login page returns.
13. Briefly present the README and GitHub repository structure.
14. End with the dashboard or Swagger UI and summarize the capstone.

## Recording Safety Checklist

- Use a temporary demo account.
- Do not reveal passwords, JWTs, database passwords, API keys, or environment variable values.
- Use a clean browser window and close unrelated tabs.
- Keep the browser at a readable zoom level.
- Verify the terminal is not displaying secrets before recording.
- Do not push commits during the recording.
