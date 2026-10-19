package com.kinostream.backend.Controllers;

import com.kinostream.backend.Models.Rating;
import com.kinostream.backend.Models.User;
import com.kinostream.backend.Models.Video;
import com.kinostream.backend.Repositories.RatingRepository;
import com.kinostream.backend.Repositories.UserRepository;
import com.kinostream.backend.Repositories.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    // Поставить или обновить оценку
    @PostMapping("/rate")
    public ResponseEntity<?> rateVideo(@RequestParam String email, @RequestParam Long videoId, @RequestParam int score) {
        if (score < 1 || score > 5) {
            return ResponseEntity.badRequest().body("Оценка должна быть от 1 до 5");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Юзер не найден"));
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Видео не найдено"));

        // Проверяем, ставил ли уже этот юзер оценку этому фильму
        Optional<Rating> existingRating = ratingRepository.findByUserAndVideo(user, video);
        Rating rating;
        if (existingRating.isPresent()) {
            rating = existingRating.get();
            rating.setScore(score); // Обновляем оценку
        } else {
            rating = new Rating();
            rating.setUser(user);
            rating.setVideo(video);
            rating.setScore(score);
        }

        ratingRepository.save(rating);

        // Возвращаем новое среднее значение
        Double avg = ratingRepository.getAverageRatingByVideo(video);
        return ResponseEntity.ok(Map.of("averageRating", avg != null ? avg : 0.0));
    }

    // Получить средний рейтинг фильма
    @GetMapping("/average")
    public ResponseEntity<?> getAverageRating(@RequestParam Long videoId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Видео не найдено"));
        Double avg = ratingRepository.getAverageRatingByVideo(video);
        int totalVotes = ratingRepository.findByVideo(video).size();

        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0); // округление до 1 знака (например, 2.1)
        response.put("totalVotes", totalVotes);

        return ResponseEntity.ok(response);
    }
}