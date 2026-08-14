package com.smarthealthcare.repository;

import com.smarthealthcare.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientIdOrderByRequestedAtDesc(Long patientId);
    List<Appointment> findByDoctorIdOrderByRequestedAtDesc(Long doctorId);
    List<Appointment> findByDoctorIdAndStatusOrderByRequestedAtDesc(Long doctorId, Appointment.Status status);
    List<Appointment> findBySlotIdAndStatus(Long slotId, Appointment.Status status);
}
