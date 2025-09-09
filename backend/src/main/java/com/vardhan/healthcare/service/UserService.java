package com.vardhan.healthcare.service;

import com.vardhan.healthcare.model.User;
import com.vardhan.healthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User register(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(password); // ⚠️ plain password (later hash)
        user.setRole(User.Role.PATIENT);
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPasswordHash().equals(password))
                .orElse(null);
    }
}
