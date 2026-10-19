package com.kinostream.backend.Controllers;

import com.kinostream.backend.Models.User;
import com.kinostream.backend.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Получить данные пользователя + статус подписки/триала
    @GetMapping
    public ResponseEntity<?> getUserProfile(@RequestParam String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Пользователь не найден");
        }

        User user = userOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("email", user.getEmail());
        response.put("username", user.getUsername() != null ? user.getUsername() : user.getEmail());

        LocalDateTime now = LocalDateTime.now();
        long daysLeft = 0;
        String subStatus = "FREE";

        // Проверяем платную подписку
        if (user.getSubscriptionEndDate() != null && user.getSubscriptionEndDate().isAfter(now)) {
            long hours = Duration.between(now, user.getSubscriptionEndDate()).toHours();
            daysLeft = (long) Math.ceil(hours / 24.0);
            if (daysLeft == 0) daysLeft = 1;
            subStatus = "PREMIUM";
        }
        // Если платной нет, проверяем 3-дневный триал
        else if (user.getTrialEndDate() != null && user.getTrialEndDate().isAfter(now)) {
            long hours = Duration.between(now, user.getTrialEndDate()).toHours();
            daysLeft = (long) Math.ceil(hours / 24.0);
            if (daysLeft == 0) daysLeft = 1;
            subStatus = "TRIAL";
        }

        response.put("subStatus", subStatus);
        response.put("daysLeft", daysLeft);

        return ResponseEntity.ok(response);
    }

    // 2. Обновить никнейм или пароль
    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newUsername = request.get("username");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Пользователь не найден");
        }

        User user = userOpt.get();

        if (newUsername != null && !newUsername.trim().isEmpty()) {
            user.setUsername(newUsername);
        }

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (oldPassword == null || !passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
                return ResponseEntity.status(400).body("Текущий пароль введен неверно");
            }
            user.setPasswordHash(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
        return ResponseEntity.ok("Профиль успешно обновлен");
    }

    // 3. Купить / продлить подписку (на 1, 3 или 6 месяцев)
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        int months = Integer.parseInt(request.getOrDefault("months", "1"));

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Пользователь не найден");
        }

        User user = userOpt.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime baseDate = now;

        // Если уже есть активная подписка или триал — прибавляем месяцы к текущей дате окончания
        if (user.getSubscriptionEndDate() != null && user.getSubscriptionEndDate().isAfter(now)) {
            baseDate = user.getSubscriptionEndDate();
        } else if (user.getTrialEndDate() != null && user.getTrialEndDate().isAfter(now)) {
            baseDate = user.getTrialEndDate();
        }

        user.setSubscriptionEndDate(baseDate.plusMonths(months));
        userRepository.save(user);

        return ResponseEntity.ok("Подписка успешно оформлена!");
    }
}