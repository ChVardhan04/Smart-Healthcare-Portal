package com.vardhan.healthcare.service;

import com.vardhan.healthcare.model.User;
import com.vardhan.healthcare.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    // Register user
    public User registerUser(String name, String email, String password, String role) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);  // ✅ plain password for now
        user.setRole(role);          // e.g., "PATIENT", "DOCTOR", "ADMIN"

        return userRepo.save(user);
    }

    // Login user
    public Optional<User> loginUser(String email, String password) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    // Find by email
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
