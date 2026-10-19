package com.kinostream.backend.Dtos;

import lombok.Data;
import java.util.List;

@Data
public class ReorderRequest {
    private String email; // Пока используем email для идентификации
    private List<Long> bookmarkIdsInNewOrder; // Массив ID в новом порядке
}