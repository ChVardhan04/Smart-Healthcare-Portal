package com.smarthealthcare.service;

import com.smarthealthcare.dto.DoctorResponse;
import com.smarthealthcare.entity.Doctor;
import com.smarthealthcare.exception.ApiException;
import com.smarthealthcare.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    @Transactional(readOnly = true)
    public List<DoctorResponse> search(String specialty, String name, String location) {
        return doctorRepository.search(
                        blankToNull(specialty), blankToNull(name), blankToNull(location))
                .stream().map(DoctorResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DoctorResponse getById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ApiException("Doctor not found", HttpStatus.NOT_FOUND));
        return DoctorResponse.from(doctor);
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
