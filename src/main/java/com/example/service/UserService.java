package com.example.service;

import com.example.dto.ApiResponse;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse<String> register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return new ApiResponse<>(false, "User already exists", null);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new ApiResponse<>(true, "User registered successfully", null);
    }

    public ApiResponse<Map<String, Object>> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty() || !passwordEncoder.matches(password, userOptional.get().getPassword())) {
            return new ApiResponse<>(false, "Invalid credentials", null);
        }

        User user = userOptional.get();
        String token = jwtUtil.generateToken(email);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", token);
        responseData.put("user", user);

        return new ApiResponse<>(true, "Login successful", responseData);
    }

    public ApiResponse<User> getUserById(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(user -> new ApiResponse<>(true, "User found", user))
                .orElse(new ApiResponse<>(false, "User not found", null));
    }

    public ApiResponse<User> updateUser(UUID id, User userDetails) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return new ApiResponse<>(false, "User not found", null);
        }

        if (userDetails.getEmail() == null || userDetails.getEmail().isBlank()) {
            return new ApiResponse<>(false, "Email is required", null);
        }
        User existingUser = userOptional.get();

        // Validate email if being updated
        if (userDetails.getEmail() != null) {
            if (userDetails.getEmail().isBlank()) {
                return new ApiResponse<>(false, "Email cannot be blank", null);
            }
            if (!userDetails.getEmail().equals(existingUser.getEmail())) {
                if (userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
                    return new ApiResponse<>(false, "Email already in use", null);
                }
            }
            existingUser.setEmail(userDetails.getEmail());
        }

        // Update other fields
        if (userDetails.getUserName() != null) {
            existingUser.setUserName(userDetails.getUserName());
        }
//        if (userDetails.getPassword() != null) {
//            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
//        }
        if (userDetails.getMobile() != null) {
            existingUser.setMobile(userDetails.getMobile());
        }

        // Handle address update carefully
        if (userDetails.getAddress() != null) {
            User.Address newAddress = userDetails.getAddress();
            User.Address existingAddress = existingUser.getAddress() != null ?
                    existingUser.getAddress() : new User.Address();

            // Update only the fields that are provided
            if (newAddress.getCity() != null) {
                existingAddress.setCity(newAddress.getCity());
            }
            if (newAddress.getStreet() != null) {
                existingAddress.setStreet(newAddress.getStreet());
            }
            if (newAddress.getState() != null) {
                existingAddress.setState(newAddress.getState());
            }
            if (newAddress.getZipCode() != null) {
                existingAddress.setZipCode(newAddress.getZipCode());
            }

            existingUser.setAddress(existingAddress);
        }

        if (userDetails.getPreferences() != null) {
            existingUser.setPreferences(userDetails.getPreferences());
        }
        if (userDetails.getRole() != null) {
            existingUser.setRole(userDetails.getRole());
        }

        try {
            User updatedUser = userRepository.save(existingUser);
            return new ApiResponse<>(true, "User updated successfully", updatedUser);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to update user: " + e.getMessage(), null);
        }
    }

    public ApiResponse<String> deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            return new ApiResponse<>(false, "User not found", null);
        }
        userRepository.deleteById(id);
        return new ApiResponse<>(true, "User deleted successfully", null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}