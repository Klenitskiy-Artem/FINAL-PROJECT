package com.kinostream.backend.Controllers;

import com.kinostream.backend.Models.Comment;
import com.kinostream.backend.Models.User;
import com.kinostream.backend.Models.Video;
import com.kinostream.backend.Repositories.CommentRepository;
import com.kinostream.backend.Repositories.UserRepository;
import com.kinostream.backend.Repositories.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    // 1. Получить все комментарии фильма (доступно ВСЕМ, даже без входа)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getComments(@RequestParam Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Видео не найдено"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy в HH:mm");

        List<Map<String, Object>> result = commentRepository.findByVideoOrderByCreatedAtDesc(video)
                .stream()
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", c.getId());
                    // Если у пользователя задан никнейм — берем его, иначе берем часть email до @
                    String authorName = (c.getUser().getUsername() != null && !c.getUser().getUsername().trim().isEmpty())
                            ? c.getUser().getUsername()
                            : c.getUser().getEmail().split("@")[0];
                    map.put("author", authorName);
                    map.put("text", c.getText());
                    map.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().format(formatter) : "");
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // 2. Добавить комментарий (только для вошедших пользователей)
    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String text = payload.get("text");
        Long videoId = Long.valueOf(payload.get("videoId"));

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(401).body("Необходимо войти в аккаунт");
        }
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Комментарий не может быть пустым");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Видео не найдено"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setVideo(video);
        comment.setText(text.trim());

        commentRepository.save(comment);
        return ResponseEntity.ok("Комментарий добавлен");
    }
}