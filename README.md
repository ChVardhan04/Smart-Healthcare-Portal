# Smart Healthcare System

A complete patient-doctor appointment platform: Java 17 + Spring Boot 3 backend, React 18
frontend, MySQL persistence. Built from the original spec, with two features made real
rather than left as TODOs:

1. **Live slot capacity** — every slot tracks `remainingSeats`, decrements on booking,
   increments back on cancellation/rejection, and is protected against overbooking by
   row-level locking + optimistic versioning (see `SlotService.reserveSeat`).
2. **Symptom-based disease prediction** — a weighted, explainable scoring engine
   (`DiseasePredictionService`) that matches submitted symptoms against a seeded
   knowledge base of 24 conditions and 44 symptoms, returning ranked likely conditions,
   the recommended specialist, and a severity flag (with an emergency warning banner
   in the UI when relevant).

This is a real, functioning implementation — not a mock. It still needs your own
MySQL credentials and (optionally) SMTP credentials before it does anything for real.

## Architecture

```
smart-healthcare/
├── backend/     Spring Boot 3 REST API (Java 17, Maven)
├── frontend/    React 18 + Vite SPA
└── docker-compose.yml   Local MySQL for development
```

Backend: RESTful JSON API, JWT auth, Spring Data JPA/Hibernate over MySQL 8.
Frontend: React Router SPA, Axios client, no external UI kit — hand-built design system.

## Quick start

### 1. Database

```bash
docker compose up -d
```

This starts MySQL 8 on `localhost:3306` with database `smart_healthcare`, user `root`,
password `root_password` (matches the defaults in `application.properties` — change
both together if you adjust one).

If you'd rather use an existing MySQL install, just create the database:
```sql
CREATE DATABASE smart_healthcare;
```

### 2. Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Runs on `http://localhost:8080`. On first boot, `DataSeeder` populates the symptom/disease
knowledge base automatically (idempotent — skips if data already exists).

**Before running in anything beyond local dev:**
- Change `app.jwtSecret` in `application.properties` to a long random string.
- Fill in real SMTP credentials, or leave them as-is — `EmailService` catches send
  failures and logs a warning instead of breaking the request, so the app works fine
  without a configured mail server, you just won't get emails.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Runs on `http://localhost:3000`, proxying `/api/*` to the backend on `:8080` (see
`vite.config.js`). Open `http://localhost:3000`.

## Feature tour

### Slot booking (live capacity)
- Doctors create slots with a date, time window, mode (online/offline), and capacity.
- Patients see `remainingSeats / capacity` on every slot in real time and can't select
  a full slot (`slot-chip-full` is disabled in the UI).
- Booking calls `SlotService.reserveSeat`, which pessimistic-locks the slot row and
  atomically decrements the seat count, throwing a 409 if it filled up in the exact
  instant between page load and click — the frontend catches that and refetches so
  the seat counts self-correct immediately.
- Doctor rejection or patient cancellation calls `releaseSeat`, putting the seat back
  into circulation.

### Disease prediction (symptom checker)
- `GET /api/predict/symptoms` returns the full symptom catalog, grouped by category
  in the UI (General, Respiratory, Cardiac, Gastrointestinal, Neurological, etc).
- `POST /api/predict` with a list of symptom names returns up to 5 ranked disease
  matches, each with a 0–100 match score, matched symptoms, recommended specialty,
  and severity (LOW / MODERATE / HIGH / EMERGENCY).
- Scoring logic (see `DiseasePredictionService`): sums matched symptom weights,
  normalizes against the disease's full symptom profile, adds a density bonus for
  diseases that explain more of what you reported, and applies a penalty if a
  "core" symptom for that disease (e.g. chest pain for a cardiac issue) is absent.
- Every result is framed as "possible conditions to discuss with a doctor," never
  a diagnosis. An EMERGENCY match surfaces a prominent warning banner telling the
  user to seek immediate care rather than rely on the tool.
- If logged in as a patient, each check is saved to `prediction_results` and can
  optionally be linked to a booking via `predictionId` on the appointment.

### Appointments & approval workflow
- Patient books → status `PENDING` → doctor approves/rejects from their dashboard →
  patient gets an email either way (`EmailService.sendAppointmentStatusEmail`).
- Online appointments start as `PENDING_PAYMENT`; a mock payment flow
  (`PaymentService`) mirrors the shape of a real Stripe/Razorpay integration
  (create-intent → client completes payment → confirm) so swapping in a real
  provider only touches that one class.

## Extending toward production

- Swap `PaymentService`'s mock provider for real Stripe/Razorpay SDK calls —
  the create-intent/confirm shape is already provider-agnostic.
- Add a real ML model behind `DiseasePredictionService` if you outgrow the
  rule-based scorer — the `DiseaseMatch` response contract wouldn't need to change.
- Add Testcontainers-based integration tests around `SlotService.reserveSeat`
  specifically — that's the highest-value place to prove the concurrency guarantees
  hold up under real parallel load, not just in code review.
- The `.env`-style secrets (JWT secret, DB password, SMTP password) should move to
  environment variables or a secrets manager before any real deployment.
