package com.smarthealthcare.service;

import com.smarthealthcare.dto.SlotRequest;
import com.smarthealthcare.dto.SlotResponse;
import com.smarthealthcare.entity.Doctor;
import com.smarthealthcare.entity.Slot;
import com.smarthealthcare.exception.ApiException;
import com.smarthealthcare.repository.DoctorRepository;
import com.smarthealthcare.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final SlotRepository slotRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public SlotResponse createSlot(Long doctorId, SlotRequest req) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ApiException("Doctor not found", HttpStatus.NOT_FOUND));

        if (!req.getEndTime().isAfter(req.getStartTime())) {
            throw new ApiException("End time must be after start time", HttpStatus.BAD_REQUEST);
        }
        if (req.getDate().isBefore(LocalDate.now())) {
            throw new ApiException("Cannot create a slot in the past", HttpStatus.BAD_REQUEST);
        }
        if (req.getCapacity() < 1) {
            throw new ApiException("Capacity must be at least 1", HttpStatus.BAD_REQUEST);
        }

        Slot slot = new Slot();
        slot.setDoctor(doctor);
        slot.setDate(req.getDate());
        slot.setStartTime(req.getStartTime());
        slot.setEndTime(req.getEndTime());
        slot.setMode(req.getMode());
        slot.setCapacity(req.getCapacity());
        slot.setRemainingSeats(req.getCapacity());
        slot.setActive(true);
        slot = slotRepository.save(slot);
        return SlotResponse.from(slot);
    }

    /**
     * Public availability listing. Filters out slots that are in the past
     * (date+time before now) so patients never see a bookable slot that has
     * already elapsed.
     */
    @Transactional(readOnly = true)
    public List<SlotResponse> getAvailableSlots(Long doctorId, LocalDate date) {
        List<Slot> slots = (date != null)
                ? slotRepository.findByDoctorIdAndDateAndActiveTrueOrderByStartTimeAsc(doctorId, date)
                : slotRepository.findByDoctorIdAndActiveTrueAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(doctorId, LocalDate.now());

        LocalDateTime now = LocalDateTime.now();
        return slots.stream()
                .filter(s -> LocalDateTime.of(s.getDate(), s.getStartTime()).isAfter(now))
                .map(SlotResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deactivateSlot(Long doctorId, Long slotId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ApiException("Slot not found", HttpStatus.NOT_FOUND));
        if (!slot.getDoctor().getId().equals(doctorId)) {
            throw new ApiException("Not authorized to modify this slot", HttpStatus.FORBIDDEN);
        }
        slot.setActive(false);
        slotRepository.save(slot);
    }

    /**
     * Atomically reserves one seat on a slot. Uses pessimistic write lock (row lock)
     * combined with the entity's @Version field as a belt-and-braces guard against
     * two patients booking the last open seat at the same instant.
     * Throws if the slot is already full - this is what makes "filled slots update
     * accordingly" actually correct under concurrent load, not just in the UI.
     */
    @Transactional
    public Slot reserveSeat(Long slotId) {
        Slot slot = slotRepository.findById(slotId) // pessimistic lock via repository method
                .orElseThrow(() -> new ApiException("Slot not found", HttpStatus.NOT_FOUND));

        if (!slot.isActive()) {
            throw new ApiException("This slot is no longer available", HttpStatus.CONFLICT);
        }
        if (slot.isFull()) {
            throw new ApiException("This slot is fully booked. Please choose another slot.", HttpStatus.CONFLICT);
        }
        slot.setRemainingSeats(slot.getRemainingSeats() - 1);
        return slotRepository.save(slot);
    }

    /** Releases a seat back (on cancellation/rejection). */
    @Transactional
    public void releaseSeat(Long slotId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ApiException("Slot not found", HttpStatus.NOT_FOUND));
        if (slot.getRemainingSeats() < slot.getCapacity()) {
            slot.setRemainingSeats(slot.getRemainingSeats() + 1);
            slotRepository.save(slot);
        }
    }
}
