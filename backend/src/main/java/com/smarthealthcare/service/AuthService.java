package com.smarthealthcare.service;

import com.smarthealthcare.config.JwtUtil;
import com.smarthealthcare.dto.*;
import com.smarthealthcare.entity.*;
import com.smarthealthcare.exception.ApiException;
import com.smarthealthcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ApiException("An account with this email already exists", HttpStatus.CONFLICT);
        }
        if (req.getRole() == User.Role.ADMIN) {
            throw new ApiException("Cannot self-register as admin", HttpStatus.FORBIDDEN);
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setPhone(req.getPhone());
        user.setRole(req.getRole());
        user = userRepository.save(user);

        Long profileId;
        if (req.getRole() == User.Role.PATIENT) {
            Patient patient = new Patient();
            patient.setUser(user);
            patient = patientRepository.save(patient);
            profileId = patient.getId();
        } else {
            if (req.getSpecialty() == null || req.getSpecialty().isBlank()) {
                throw new ApiException("Specialty is required for doctor registration", HttpStatus.BAD_REQUEST);
            }
            Doctor doctor = new Doctor();
            doctor.setUser(user);
            doctor.setSpecialty(req.getSpecialty());
            doctor.setQualifications(req.getQualifications());
            doctor.setClinicAddress(req.getClinicAddress());
            doctor.setLocation(req.getLocation());
            doctor.setConsultationFee(req.getConsultationFee());
            doctor.setOnlineFee(req.getOnlineFee());
            doctor.setYearsExperience(req.getYearsExperience());
            doctor = doctorRepository.save(doctor);
            profileId = doctor.getId();
        }

        emailService.sendWelcomeEmail(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole().name(), user.getId(), profileId);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
        if (!user.isActive()) {
            throw new ApiException("This account has been deactivated", HttpStatus.FORBIDDEN);
        }

        Long profileId;
        if (user.getRole() == User.Role.PATIENT) {
            profileId = patientRepository.findByUserId(user.getId()).map(Patient::getId).orElse(null);
        } else if (user.getRole() == User.Role.DOCTOR) {
            profileId = doctorRepository.findByUserId(user.getId()).map(Doctor::getId).orElse(null);
        } else {
            profileId = null;
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole().name(), user.getId(), profileId);
    }
}
