# Language School LMS (Demo)

A full-stack Learning Management System for language school with role-based access for Students, Instructors, and Admins.

---

## Tech Stack

- **Frontend**: React + TypeScript + Vite + Tailwind CSS
- **Backend**: Java + Spring Boot + Maven
- **Database**: MySQL
- **Authentication**: Firebase Authentication

---

## Prerequisites

Before running the project, ensure you have:

- **Node.js** 18+ and npm
- **Java JDK** 25
- **Maven** 3.8+
- **MySQL** 8.0+
- **Firebase** account with project setup

---

## Installation & Setup

### Step 1: Clone Repository

```bash
git clone https://github.com/RubyXZZZ/education-learning-management-system
```

### Step 2: Database Setup

Create MySQL database:

```sql
CREATE DATABASE lms_db;
```

### Step 3: Backend Setup

```bash
cd backend

# Edit src/main/resources/application.yml
# Configure:
#   - spring.datasource.url=jdbc:mysql://localhost:3306/lms_db
#   - spring.datasource.username=your_mysql_username
#   - spring.datasource.password=your_mysql_password
#   - Firebase service account credentials

# Run Spring Boot application
mvn clean install
mvn spring-boot:run
```

Backend will start on: **http://localhost:8080**

### Step 4: Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Create .env file with Firebase config
# VITE_API_BASE_URL=http://localhost:8080/api
# VITE_FIREBASE_API_KEY=your_firebase_api_key
# VITE_FIREBASE_AUTH_DOMAIN=your_project.firebaseapp.com
# VITE_FIREBASE_PROJECT_ID=your_project_id
# VITE_FIREBASE_STORAGE_BUCKET=your_project.appspot.com
# VITE_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
# VITE_FIREBASE_APP_ID=your_app_id

# Run development server
npm run dev
```

Frontend will start on: **http://localhost:3030**

---

## Project Structure

```
lms/
├── frontend/                 # React TypeScript SPA
│   ├── src/
│   │   ├── components/       # Reusable UI components
│   │   ├── pages/            # Page-level components
│   │   ├── services/         # API clients & Firebase
│   │   ├── types/            # TypeScript interfaces
│   │   ├── contexts/         # React contexts
│   │   ├── constants/        # App constants
│   │   ├── App.tsx
│   │   ├── index.html
│   │   └── main.tsx
│   ├── package.json
│   └── vite.config.ts
│
└── backend/                  # Spring Boot REST API
    ├── src/main/java/io/rubyxzzz/lms/backend/
    │   ├── model/            # JPA entities
    │   ├── dto/              # Data transfer objects
    │   ├── repository/       # Data access layer
    │   ├── service/          # Business logic
    │   ├── controller/       # REST endpoints
    │   ├── mapper/           # Mapping between DTOs and entities
    │   ├── util/              # Utility helpers
    │   ├── security/         # Security configuration
    │   ├── config/           # Spring configurations
    │   └── exception/        # Exception handling
    ├── src/main/resources/
    │   └── application.yml
    └── pom.xml
```

---

## User Roles & Access

### Admin (& SuperAdmins)
- Manage users - Students, Instructors, Admins(superadmin only)
- Manage courses and sections
- System settings (superadmin only)
- Manage enrollments

### Instructor
- View assigned sections
- Manage course content (modules, pages, assignments)
- Grade student submissions

### Student
- View enrolled courses
- Submit assignments
- Access course materials
- Track grades

---

## API Documentation

Backend API runs on `http://localhost:8080/api`



## Note

This project is for educational purposes (Final Project).

