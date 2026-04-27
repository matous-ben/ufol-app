# ⚽ UFoL - University Football League App

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

**UFoL App** is a full-stack web application built to digitalize, manage, and display the standings, matches, and teams of a University Football League. 

Developed as a comprehensive Web Technologies project, this application demonstrates the complete software development lifecycle—from initial system design (UML, ERD, Use Cases) to a containerized, production-ready implementation using modern Java backend architecture and server-side rendering.

---

## ✨ Key Features

<img width="2849" height="1528" alt="Screenshot 2026-04-27 214849" src="https://github.com/user-attachments/assets/6da24011-7e41-42bd-85f5-880d7b1ce410" />

---

<img width="2844" height="1477" alt="Screenshot 2026-04-27 214942" src="https://github.com/user-attachments/assets/4d420e27-9efa-4f10-a061-809092c4f288" />

---

<img width="2844" height="1353" alt="Screenshot 2026-04-27 215025" src="https://github.com/user-attachments/assets/bf48c356-caf1-4c76-8001-2042f18b6a55" />

---

### 🌍 Public Portal (Fan Facing)
* **Live Standings:** Automatically calculated and dynamically sorted league tables based on real-time match results.
* **Match Schedule:** Interactive calendar for upcoming fixtures and historical match data.
* **Team Profiles:** Detailed views of participating universities, team rosters, and player statistics.
* **Custom Responsive UI:** Clean, mobile-first interface built from scratch with HTML5, CSS3, and Thymeleaf.

### 🔒 Admin Dashboard (Secured)
* **Authentication & Security:** Secured with Spring Security, leveraging BCrypt for password hashing and robust CSRF protection.
* **League Management:** Full CRUD capabilities for organizers to manage Seasons, Teams, Matches, and Venues.
* **Automated Data Processing:** Match result entries immediately trigger backend recalculations of team points, goal differences, and league rankings.

---

## 🛠️ Tech Stack

**Backend & Data Layer:**
* **Java 21** - Utilizing modern language features.
* **Spring Boot 4.0.5** - Core application framework.
* **Spring Data JPA (Hibernate)** - Object-Relational Mapping (ORM) for database interactions.
* **PostgreSQL 18** - Relational database for persistent, structured data storage.
* **Spring Security** - Managing authentication and authorization.

**Frontend:**
* **Thymeleaf** - Server-side HTML templating engine.
* **HTML5 / CSS3 / Vanilla JS** - For lightweight, fast client-side rendering.

**DevOps & Infrastructure:**
* **Docker & Docker Compose** - Ensuring consistent, isolated local development and easy deployment.
* **Maven** - Dependency management and application build process.

---

## 🛡️ Engineering & Architecture Decisions

This project goes beyond standard implementations by utilizing **Architecture Decision Records (ADRs)** to document technical choices and trade-offs. 

* *Example:* See [ADR-003](docs/ADRs/ADR-003.md) for the technical reasoning behind choosing native Spring Security session management over an external OAuth2 provider for organizer authentication.
* The system architecture is backed by thorough preliminary design, including Entity-Relationship Diagrams (ERDs) and Use Case modeling to ensure normalized data structures and logical user flows.

---

## 🚀 Getting Started

### Prerequisites
* [Docker](https://www.docker.com/products/docker-desktop/) and Docker Compose (Recommended)
* *OR* Java 21 and a local PostgreSQL instance.

### Option 1: Run with Docker (Recommended)
This spins up the Spring Boot application and the PostgreSQL database in an isolated container network.

1. Clone the repository:
   ```bash
   git clone [https://github.com/matous-ben/ufol-app.git](https://github.com/matous-ben/ufol-app.git)
   cd ufol-app

2. Start the application:
   ```bash
   docker-compose up -d

3. Access the application at http://localhost:8080

### Option 2: Run Locally (Maven)
If you prefer running the application directly on your host machine:

1. Configure your local PostgreSQL credentials in [src/main/resources/application.properties](src/main/resources/application.properties)

2. Run the Spring Boot application:
```bash
./mvnw spring-boot:run
```
## 👨‍💻 Author
Matouš Benedikt [@matous-ben](https://github.com/matous-ben)
