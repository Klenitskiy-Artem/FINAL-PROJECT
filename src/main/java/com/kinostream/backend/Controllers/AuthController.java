package com.kinostream.backend.Controllers;

import com.kinostream.backend.Dtos.RegisterRequest;
import com.kinostream.backend.Services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            authService.registerUser(request.getEmail(), request.getUsername(), request.getPassword());
            return ResponseEntity.ok("Пользователь успешно зарегистрирован! Выдан пробный период на 3 дня.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody com.kinostream.backend.Dtos.LoginRequest request) {
        try {
            com.kinostream.backend.Dtos.AuthResponse response = authService.loginUser(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}