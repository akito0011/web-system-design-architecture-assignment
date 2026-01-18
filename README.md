# Hotel Management System

A complete **Hotel Management System** built with **Spring Boot 4** for the *Web Systems Design and Architecture* course at the **University of Palermo (UNIPA)**.

This full-stack web application implements a hotel reservation and management platform with role-based access control, home automation features, and comprehensive reporting capabilities.

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-brightgreen?style=flat-square&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue?style=flat-square&logo=mysql)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Template%20Engine-green?style=flat-square)
![Spring Security](https://img.shields.io/badge/Spring%20Security-Authentication-red?style=flat-square)

---

## Project Requirements

This project was developed to fulfill the following requirements set by the professor:

### Functional Requirements
| Requirement | Description | Status |
|-------------|-------------|--------|
| **Multi-role System** | Support for at least 3 user roles (Admin, Staff, Customer) with different permissions | ✅ |
| **User Registration & Authentication** | Secure registration and login with password encryption | ✅ |
| **Room Booking System** | Complete reservation flow with availability checking | ✅ |
| **Check-in/Check-out Management** | Guest registration with ID document collection | ✅ |
| **City Tax Calculation** | Automatic tourist tax calculation with exemptions (under 12, over 85) | ✅ |
| **XML Report Generation** | Police notification reports and tax reports in XML format | ✅ |
| **Additional Services** | Ability to add extra services to reservations | ✅ |
| **Staff Management** | Admin can create and manage staff accounts | ✅ |
| **Cleaning Tracking** | Housekeeping staff can mark rooms as cleaned with history log | ✅ |
| **Home Automation** | Control room devices (lights, blinds, thermostat) | ✅ |

### Technical Requirements
| Requirement | Implementation | Status |
|-------------|----------------|--------|
| **Spring Boot Application** | Spring Boot 4.0 with Maven | ✅ |
| **Layered Architecture** | Controller → Service → Repository → Entity | ✅ |
| **Spring MVC** | Web controllers with Thymeleaf templates | ✅ |
| **Spring Security** | Role-based authorization with BCrypt password encoding | ✅ |
| **Spring Data JPA** | ORM with Hibernate and MySQL | ✅ |
| **Bean Validation** | Form validation with Jakarta Validation annotations | ✅ |
| **Transaction Management** | `@Transactional` for data integrity | ✅ |
| **Dependency Injection** | Constructor-based injection throughout | ✅ |

---

##  Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│         (Controllers + Thymeleaf Templates)                  │
│     web/controller/*.java + templates/*.html                 │
├─────────────────────────────────────────────────────────────┤
│                     SERVICE LAYER                            │
│              (Business Logic)                                │
│            domain/service/*.java                             │
├─────────────────────────────────────────────────────────────┤
│                   REPOSITORY LAYER                           │
│              (Data Access)                                   │
│           domain/repository/*.java                           │
├─────────────────────────────────────────────────────────────┤
│                     ENTITY LAYER                             │
│            (JPA Entities)                                    │
│            domain/entity/*.java                              │
├─────────────────────────────────────────────────────────────┤
│                      DATABASE                                │
│                      (MySQL)                                 │
└─────────────────────────────────────────────────────────────┘
```

---

##  Project Structure

```
src/
├── main/
│   ├── java/it/unipa/progettowsda/
│   │   ├── ProgettoWsdaApplication.java    # Application entry point
│   │   ├── config/
│   │   │   └── SecurityConfig.java         # Spring Security configuration
│   │   ├── security/
│   │   │   ├── CustomUserDetailsService.java
│   │   │   └── CustomAuthenticationSuccessHandler.java
│   │   ├── domain/
│   │   │   ├── entity/                     # JPA Entities
│   │   │   │   ├── Camera.java             # Room
│   │   │   │   ├── Prenotazione.java       # Reservation
│   │   │   │   ├── Ospite.java             # Guest
│   │   │   │   ├── Utente.java             # User
│   │   │   │   ├── Struttura.java          # Property/Hotel
│   │   │   │   ├── Servizio.java           # Extra Service
│   │   │   │   ├── PrenotazioneServizio.java
│   │   │   │   ├── StoricoPulizie.java     # Cleaning History
│   │   │   │   ├── Luce.java               # Light (IoT)
│   │   │   │   ├── Tapparella.java         # Blind (IoT)
│   │   │   │   ├── Termostato.java         # Thermostat (IoT)
│   │   │   │   └── enumerazioni/           # Enums
│   │   │   ├── repository/                 # Spring Data JPA Repositories
│   │   │   └── service/                    # Business Logic Services
│   │   │       ├── PrenotazioneService.java
│   │   │       ├── UtenteService.java
│   │   │       ├── StaffService.java
│   │   │       └── DomoticaService.java
│   │   └── web/
│   │       ├── controller/                 # MVC Controllers
│   │       │   ├── AuthController.java
│   │       │   ├── ClienteController.java
│   │       │   ├── PrenotazioneController.java
│   │       │   ├── GestoreController.java
│   │       │   ├── StaffController.java
│   │       │   └── DomoticaController.java
│   │       └── form/                       # DTOs for form binding
│   └── resources/
│       ├── application.properties
│       └── templates/                      # Thymeleaf HTML templates
│           ├── public/                     # Login, Register
│           ├── cliente/                    # Customer views
│           ├── gestore/                    # Admin views
│           └── staff/                      # Staff views
└── test/                                   # Unit & Integration tests
```

---

##  User Roles

| Role | Access | Capabilities |
|------|--------|--------------|
| **ADMIN** | `/admin/**` | Dashboard overview, manage all reservations, force check-in/out, cancel bookings, generate XML reports, manage staff accounts, view cleaning history |
| **STAFF** | `/staff/**` | View rooms needing cleaning, mark rooms as cleaned, view occupied rooms |
| **CLIENTE** | `/cliente/**` | Search properties, book rooms, manage reservations, perform check-in with guest data, checkout, control room IoT devices, add booking notes |

---

##  Features

###  Room Search & Booking
- Search available rooms by city, dates, and guest count
- Browse properties and select specific rooms
- Add extra services to reservations
- Automatic price calculation with tourist tax

### Check-in Process
- Register all guests with personal information
- Document verification (ID type and number)
- Automatic tax exemption calculation (age < 12 or > 85)
- Real-time price adjustment based on actual exemptions vs. declared

###  Home Automation (IoT)
- **Lights**: Toggle individual room lights on/off
- **Blinds**: Raise, lower, or stop blinds at current position
- **Thermostat**: Adjust temperature, switch between heating/cooling/off modes

###  Reports
- **Police Report (Questura)**: XML export of all guests grouped by property
- **Tourist Tax Report**: XML export with exemption breakdown by type

###  Staff Management
- Admin can create new staff accounts
- Staff can mark rooms as cleaned
- Complete cleaning history log with timestamps

---

##  Tech Stack

| Component | Technology |
|-----------|------------|
| **Backend** | Java 21, Spring Boot 4.0 |
| **Web Framework** | Spring MVC |
| **Security** | Spring Security with BCrypt |
| **ORM** | Spring Data JPA + Hibernate |
| **Database** | MySQL |
| **Template Engine** | Thymeleaf |
| **Validation** | Jakarta Bean Validation |
| **Build Tool** | Maven |
| **Server** | Embedded Tomcat |

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- MySQL 8.0+

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/progettoWSDA.git
   cd progettoWSDA
   ```

2. **Create the MySQL database**
   ```sql
   CREATE DATABASE hotelmanagement;
   ```

3. **Configure database credentials**
   
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hotelmanagement?serverTimezone=UTC
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the application**
   ```
   http://localhost:8080
   ```

---

##  API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/login` | Login page |
| POST | `/login` | Process login |
| GET | `/register` | Registration page |
| POST | `/register` | Create new user account |
| GET | `/logout` | Logout |

### Customer (`/cliente`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/cliente/dashboard` | Customer dashboard |
| GET | `/cliente/cerca` | Room search form |
| POST | `/cliente/cerca` | Search results |
| GET | `/cliente/checkin/{id}` | Check-in form |
| POST | `/cliente/checkout` | Complete checkout |
| GET | `/cliente/domotica/{id}` | IoT control panel |

### Admin (`/admin`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/dashboard` | Admin dashboard |
| GET | `/admin/gestione-staff` | Staff management |
| POST | `/admin/crea-staff` | Create staff account |
| POST | `/admin/cancella-prenotazione` | Cancel reservation |
| GET | `/admin/download-report-questura` | Download police report |
| GET | `/admin/download-report-tassa` | Download tax report |

### Staff (`/staff`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/staff/dashboard` | Staff dashboard |
| POST | `/staff/segna-pulita` | Mark room as cleaned |

---

## License

This project was developed for educational purposes as part of the **Web Systems Design and Architecture** course at the University of Palermo, Academic Year 2025-26.

---

