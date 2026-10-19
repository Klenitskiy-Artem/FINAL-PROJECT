package com.kinostream.backend.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private boolean hasPremiumAccess; // true - может смотреть 1080p, false - только 480p
}