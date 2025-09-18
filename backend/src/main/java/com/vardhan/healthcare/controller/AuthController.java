package com.vardhan.healthcare.controller;

import com.vardhan.healthcare.dto.RegisterRequest;
import com.vardhan.healthcare.model.User;
import com.vardhan.healthcare.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserRepo userRepo;

    // ✅ Register user
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepo.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password()); // plain password for now
        user.setRole("PATIENT"); // default role unless registering doctor/admin

        userRepo.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    // ✅ Login user
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RegisterRequest request) {
        Optional<User> userOpt = userRepo.findByEmail(request.email());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(request.password())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        return ResponseEntity.ok(user);
    }
}
