# ISTYLE

This guide explains how to run the full application (database, backend API, and frontend UI) on your local machine.

## Prerequisites
- Docker Desktop (for Postgres)
- Java 21 (JDK)
- Node.js 18+ and npm
- PowerShell or terminal

## 1) Start the database (Postgres)
1. Open PowerShell in the project root:
   - C:\Users\ggera\Desktop\PracaInż\ISTYLE
2. Start Postgres with Docker Compose:
   - Command: `docker compose -f backend\docker-compose.yml up -d`
3. Verify it’s running on localhost:5433 (Docker Desktop UI or `docker ps`).

Database configuration expected by the backend:
- Host: localhost
- Port: 5433
- DB: db
- User: postgres
- Password: postgres

If you need to change these, update backend\src\main\resources\application.yml accordingly.

## 2) Run the backend (Spring Boot)
1. Open a new terminal and go to the backend folder:
   - `cd backend`
2. Using Maven Wrapper (recommended):
   - Start directly: `.\u200bmvnw.cmd spring-boot:run`
   - Alternative build + run: `.\u200bmvnw.cmd clean package` then `java -jar target\backend-0.0.1-SNAPSHOT.jar`
3. The app should start on http://localhost:8080 and auto-create/update schema (spring.jpa.hibernate.ddl-auto=update).

Notes:
- CORS is configured to allow http://localhost:3000 (see AppConfig.corsFilter).
- Static files are served from backend/src/main/resources/static under path /images/**.
- JWT secret and other keys are already set in application.yml.

## 3) Run the frontend (React)
1. Open a new terminal and go to the frontend folder:
   - `cd frontend`
2. Install dependencies:
   - `npm install`
3. Start the dev server:
   - `npm start`
4. Open http://localhost:3000 in your browser.

Notes:
- Axios baseURL is http://localhost:8080 (frontend\src\axiosConfig.js). It attaches `Authorization: Bearer <token>` from localStorage if present.
- package.json also has a proxy to 8080, but axios uses an absolute baseURL so the proxy is not required for API calls.

## 4) First-time usage flow
- Register: Use the UI (Register page) or POST to `/api/auth/register`.
- Login: Use UI (Login page) or POST `/api/auth/login`. The token is stored as `localStorage['token']`.
- After login, navigate features like Wardrobe, Outfit Creator, etc. All API requests will include your JWT.

## 5) Useful endpoints
- POST `/api/auth/register`
- POST `/api/auth/login`
- Example secured endpoints used by the frontend:
  - GET `/api/clothes`
  - GET `/api/clothes/categories`
  - PUT `/api/clothes/{id}`

## 6) Troubleshooting
- Port conflicts: change DB port in `backend\docker-compose.yml` or stop other services on 5433/8080/3000.
- Database connection: ensure Docker service is up; if using a different DB, update `application.yml` (url, username, password).
- CORS errors: ensure you’re on http://localhost:3000; CORS is allowed for that origin.
- Token issues: if you get 401/403, clear `localStorage.token` and log in again.
- Windows paths: run commands from the correct folders; use backslashes in paths.

## 7) Optional: Run via IntelliJ IDEA
- Open the ISTYLE project, select the backend module.
- Ensure JDK 21 is set for the backend.
- Create a Spring Boot run configuration for the main application class (package `com.istyle.backend`) and run it.

## ERD

![ERD_final](https://github.com/Geruclkdz/ISTYLE/assets/97120136/fb26df79-8284-46ce-b8f9-3104ae902782)

