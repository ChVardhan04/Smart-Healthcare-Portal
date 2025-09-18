-- Insert demo users (2 doctors, 1 patient, 1 admin)
INSERT INTO users (name, email, password_hash, role)
VALUES 
('Dr. Ramesh', 'ramesh@hospital.com', 'pass123', 'DOCTOR'),
('Dr. Priya', 'priya@hospital.com', 'pass123', 'DOCTOR'),
('Vardhan', 'vardhan@mail.com', 'pass123', 'PATIENT'),
('Admin', 'admin@hospital.com', 'admin123', 'ADMIN');

-- Insert doctors linked to users (user_id refers to users.id)
INSERT INTO doctors (name, specialization, user_id)
VALUES
('Dr. Ramesh', 'Cardiologist', 1),
('Dr. Priya', 'Diabetologist', 2);

-- Insert slots for doctors (tomorrow's date, adjust as needed)
INSERT INTO doctor_slots (doctor_id, slot_time, is_booked)
VALUES
(1, '2025-09-18 09:00:00', FALSE),
(1, '2025-09-18 10:00:00', FALSE),
(1, '2025-09-18 11:00:00', FALSE),
(2, '2025-09-18 14:00:00', FALSE),
(2, '2025-09-18 15:00:00', FALSE);

-- Insert a sample report for patient
INSERT INTO reports (patient_id, title, details, created_at)
VALUES
(3, 'Blood Test Report', 'All values normal.', NOW());
