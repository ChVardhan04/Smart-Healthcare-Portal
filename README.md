# Smart Healthcare System

A full-stack healthcare app for patients and doctors with:

* Appointment booking (online/offline)
* Doctor slot management
* Approval / rejection workflow
* Email notifications
* Payment-ready integration for online consultations

What this really is: a complete starter you can push to GitHub, run locally, and extend. Below is a ready-to-use `README.md` you can copy into your repo.

---

# Smart Healthcare System

## Table of contents

* About
* Features
* Tech stack
* Architecture overview
* Quick start

  * Backend
  * Frontend
* Configuration / environment
* Database schema (entities)
* Important API endpoints (summary)
* Email flow
* Payment integration notes
* Deployment tips
* Tests
* TODO / roadmap
* Contributing
* License

---

## About

This project is a minimal, practical implementation of a patient-doctor appointment system. It supports searching doctors, booking appointments (online or offline), doctor-managed slots, approval/rejection workflows, email notifications, and is ready for payments for online consultations.

## Features

* Patient

  * Browse doctors by specialty, location, name
  * View doctor availability (slots)
  * Request appointment (choose online or offline)
  * Receive email notifications and status updates
* Doctor

  * Create and manage slots (date, start, end, mode, capacity)
  * View pending appointment requests
  * Approve or reject requests with optional comments
  * Receive email notifications for new requests and status changes
* Admin (optional)

  * CRUD doctors and patients, view analytics
* Payment-ready

  * Hooks for integrating payment provider (e.g., Stripe, Razorpay)
* Email

  * Spring Mail templates for appointment notifications

## Tech stack

**Backend**

* Java 17
* Spring Boot 3
* Spring Data JPA (Hibernate)
* MySQL 8
* Spring Mail
* Lombok
* Maven

**Frontend**

* React (Vite or CRA)
* Axios
* Bootstrap or Material UI

**DB**

* MySQL 8

---

## Architecture overview

* RESTful Spring Boot backend exposing JSON APIs
* React frontend consuming APIs via Axios
* MySQL for persistence
* Email via Spring Mail (SMTP)
* Payment integration done via client + backend signed endpoints

---

## Quick start

### Backend

1. Setup MySQL database, create schema `smart_healthcare` (or any name you configure).
2. Copy `.env` values to `application.properties` or use environment variables.
3. Build and run

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Default server: `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install
npm start
```

Default site: `http://localhost:3000` (or configured port)

---

## Configuration / environment variables

Store these in `src/main/resources/application.properties` or pass as environment variables.

Example `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_healthcare?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=your@mail.com
spring.mail.password=mail_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# JWT / security (if used)
app.jwtSecret=replace-this-secret
app.jwtExpirationMs=86400000

# Payment provider keys (example)
payment.provider=stripe
stripe.api.key=sk_test_...
```

Keep secrets out of Git. Use GitHub secrets for deployments.

---

## Database schema (entities)

High level entity list and key fields:

* User (common base or separate Patient/Doctor)

  * id, name, email, passwordHash, role (PATIENT/DOCTOR/ADMIN), phone

* Patient

  * id (FK to User), address, dob, medicalNotes (optional)

* Doctor

  * id (FK to User), specialty, qualifications, clinicAddress, consultationFee, onlineFee

* Slot

  * id, doctorId, date, startTime, endTime, mode (ONLINE/OFFLINE), capacity, remainingSeats, isActive

* Appointment

  * id, patientId, doctorId, slotId, status (PENDING/APPROVED/REJECTED/CANCELLED/COMPLETED), mode, paymentStatus, notes, requestedAt, decidedAt

* Payment (if integrated)

  * id, appointmentId, provider, providerPaymentId, amount, status

Create JPA entities matching these fields. Use DTOs for API responses to avoid exposing sensitive data.

---

## Important API endpoints (examples)

Base: `GET /api/health` -> health check

Auth:

* `POST /api/auth/register` -> register user
* `POST /api/auth/login` -> returns JWT

Doctors:

* `GET /api/doctors` -> list doctors with filters (specialty, name, location)
* `GET /api/doctors/{id}` -> doctor detail with next available slots
* `POST /api/doctors` -> admin/doctor create (secure)
* `PUT /api/doctors/{id}` -> update

Slots:

* `GET /api/doctors/{doctorId}/slots?date=YYYY-MM-DD` -> available slots
* `POST /api/doctors/{doctorId}/slots` -> doctor creates slot

  * body: `{ date, startTime, endTime, mode, capacity }`
* `PUT /api/slots/{slotId}` -> update slot
* `DELETE /api/slots/{slotId}`

Appointments:

* `POST /api/appointments` -> patient requests appointment

  * body: `{ patientId, doctorId, slotId, mode, notes }`
* `GET /api/appointments/{id}` -> get appointment
* `GET /api/patients/{patientId}/appointments` -> patient history
* `GET /api/doctors/{doctorId}/appointments?status=PENDING` -> doctor pending requests
* `PUT /api/appointments/{id}/status` -> doctor approves/rejects

  * body: `{ status: "APPROVED" | "REJECTED", comment: "..." }`
* `POST /api/appointments/{id}/cancel` -> cancel logic

Payments:

* `POST /api/payments/create-intent` -> create payment intent for online consult
* `POST /api/payments/confirm` -> confirm and save payment

Email notifications are triggered on appointment creation, approval, rejection, cancellation, and payment confirmation.

---

## Email flow (Spring Mail)

Templates:

* `appointment-request.html` -> sent to doctor with patient details and link to approve/reject
* `appointment-status.html` -> sent to patient with approval/rejection and notes
* `payment-receipt.html` -> paid appointment receipt

Implementation tips:

* Use Thymeleaf or simple template replacement for HTML emails
* Keep subject templates configurable
* Add a queue or async executor for mail sending to avoid blocking requests

Example service signature

```java
public interface EmailService {
  void sendAppointmentRequestEmail(Doctor doctor, Appointment appointment);
  void sendAppointmentStatusEmail(Patient patient, Appointment appointment);
  void sendPaymentReceiptEmail(Patient patient, Payment payment);
}
```

---

## Payment integration notes

* Keep core appointment creation independent of payment. For online consultations, mark appointment as PENDING\_PAYMENT until payment succeeds. Once payment confirms, update paymentStatus and allow doctor to approve.
* Recommended flow:

  1. Patient selects slot and chooses online consult
  2. Frontend hits `POST /api/payments/create-intent` -> backend calls payment provider and returns client token
  3. Frontend completes client-side payment flow
  4. On success, frontend calls `POST /api/payments/confirm` -> backend verifies and marks appointment paymentStatus=PAID
* Use Stripe or Razorpay SDKs. Never store raw card data on your servers.

---

## Frontend notes

* Use React with routing: pages for Doctor list, Doctor detail (slots), Patient dashboard, Doctor dashboard, Login/Register.
* Axios default base URL: `http://localhost:8080/api`
* Authentication: store JWT in memory or secure httpOnly cookie. If you use localStorage, be aware of XSS risk.
* Example components:

  * `DoctorList`, `DoctorCard`, `DoctorDetail`, `SlotPicker`, `AppointmentForm`, `PatientAppointments`, `DoctorAppointments`
* Use modal dialogs for booking flow, show clear states: Pending -> Awaiting Approval -> Approved/Rejected.

---

## Testing

* Backend: unit tests for services and controllers using JUnit 5 and Mockito.
* Integration tests with Testcontainers MySQL if possible.
* Frontend: basic Jest + React Testing Library for critical forms and flows.

---

## Deployment tips

* Build backend jar and bundle with Docker
* Use environment variables for DB, SMTP, JWT secrets
* Use managed DB (RDS / Cloud SQL) in production
* Use a secure SMTP provider or transactional email service (SendGrid, Mailgun)
* For SSL, use reverse proxy or host on platform that manages TLS

---

## Example data (SQL)

```sql
INSERT INTO doctor (id, name, specialty, consultationFee) VALUES (1, 'Dr. Srinivas', 'Cardiology', 500);
INSERT INTO patient (id, name) VALUES (1, 'Ravi Kumar');
-- create slots
INSERT INTO slot (doctor_id, date, start_time, end_time, mode, capacity, remaining_seats, is_active) 
VALUES (1, '2025-09-20', '09:00:00', '09:30:00', 'ONLINE', 1, 1, true);
```

---

## TODO / Roadmap

* Add role-based access with Spring Security (JWT)
* Add cancellation policies and time buffers
* Add video consultation integration (Jitsi / Twilio)
* Add analytics and reporting for admin
* Mobile-friendly UI
* Add unit and integration test coverage targets

---

## Contributing

* Fork, make a feature branch, write tests, open a PR
* Keep commits small and focused
* Use clear commit messages

---


