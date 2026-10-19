package com.kinostream.backend.Services;

import com.kinostream.backend.Models.Role;
import com.kinostream.backend.Models.User;
import com.kinostream.backend.Repositories.RoleRepository;
import com.kinostream.backend.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(String email, String username, String rawPassword) {
        // 1. Проверяем, не занят ли email
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Пользователь с таким email уже существует!");
        }

        // 2. Создаем нового пользователя
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword)); // Шифруем пароль

        // 3. Выдаем 3 дня пробного периода
        user.setTrialEndDate(LocalDateTime.now().plusDays(3));

        // 4. Назначаем базовую роль
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Роль ROLE_USER не найдена в БД"));
        user.getRoles().add(userRole);

        // 5. Сохраняем в базу данных
        return userRepository.save(user);
    }
    public com.kinostream.backend.Dtos.AuthResponse loginUser(String email, String rawPassword) {
        // 1. Ищем пользователя по email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден!"));

        // 2. Сверяем введенный пароль с зашифрованным в БД
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new RuntimeException("Неверный пароль!");
        }

        // 3. Проверяем доступ к Premium (720p/1080p)
        boolean hasPremium = checkPremiumAccess(user);

        return new com.kinostream.backend.Dtos.AuthResponse("Вход выполнен успешно!", hasPremium);
    }

    // Вспомогательный метод для проверки подписки (по нашему ТЗ)
    public boolean checkPremiumAccess(User user) {
        LocalDateTime now = LocalDateTime.now();

        boolean isTrialActive = user.getTrialEndDate() != null && user.getTrialEndDate().isAfter(now);
        boolean isSubActive = user.getSubscriptionEndDate() != null && user.getSubscriptionEndDate().isAfter(now);

        return isTrialActive || isSubActive;
    }
}