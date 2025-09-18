package com.vardhan.healthcare.repo;

import com.vardhan.healthcare.model.DoctorSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DoctorSlotRepo extends JpaRepository<DoctorSlot, Long> {
    List<DoctorSlot> findByDoctorIdAndBookedFalse(Long doctorId);
}
