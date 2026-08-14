package com.smarthealthcare.repository;

import com.smarthealthcare.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByDoctorIdAndDateAndActiveTrueOrderByStartTimeAsc(Long doctorId, LocalDate date);
    List<Slot> findByDoctorIdAndActiveTrueAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(Long doctorId, LocalDate fromDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Slot> findById(Long id);
}
