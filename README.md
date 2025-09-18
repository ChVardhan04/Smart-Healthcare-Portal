# Smart Healthcare System 

A full-stack healthcare application for **patients** and **doctors** with:
- Appointment booking (online/offline)
- Doctor slot management
- Approval/rejection workflow
- Email notifications
- Payment-ready integration for online consultations

---

##  Tech Stack
**Backend**
- Java 17 / Spring Boot 3
- Spring Data JPA (Hibernate + MySQL)
- Spring Mail
- Lombok
- Maven

**Frontend**
- React.js (Vite or CRA)
- Axios
- Bootstrap / Material-UI (optional)

**Database**
- MySQL 8

---

##  Project Modules
- **Patient Dashboard**: browse doctors, view available slots, request appointments  
- **Doctor Dashboard**: view pending appointments, approve/reject, manage slots  
- **Admin**: (optional) manage doctors/patients  

---

##  Setup

### 1. Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
