package com.kinostream.backend.Controllers;

import com.kinostream.backend.Models.User;
import com.kinostream.backend.Models.Video;
import com.kinostream.backend.Repositories.RatingRepository;
import com.kinostream.backend.Repositories.UserRepository;
import com.kinostream.backend.Repositories.VideoRepository;
import com.kinostream.backend.Services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    // --- Методы из Epic 3 (Каталог) ---
    @GetMapping
    public ResponseEntity<List<Video>> getAllVideos() {
        return ResponseEntity.ok(videoRepository.findAll());
    }

    @GetMapping("/latest")
    public ResponseEntity<List<Video>> getLatestVideos() {
        return ResponseEntity.ok(videoRepository.findTop10ByOrderByIdDesc());
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<Video>> getVideosByCategory(@PathVariable String categoryName) {
        return ResponseEntity.ok(videoRepository.findByCategory_Name(categoryName));
    }

    @GetMapping("/genre/{genreName}")
    public ResponseEntity<List<Video>> getVideosByGenre(@PathVariable String genreName) {
        return ResponseEntity.ok(videoRepository.findByGenres_Name(genreName));
    }

    // --- НОВЫЕ МЕТОДЫ ИЗ Epic 4 (Плеер и страница) ---

    // 1. Получить детальную страницу видео (описание, год, страна и т.д.)
    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideoDetails(@PathVariable Long id) {
        return videoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 2. Бизнес-логика плеера (проверка качества)
    @GetMapping("/{id}/play")
    public ResponseEntity<?> playVideo(@PathVariable Long id,
                                       @RequestParam int quality,
                                       @RequestParam(required = false) String email) {

        // 1. ПРОВЕРКА ПРАВ ДОСТУПА
        // Если просят качество выше 480 (то есть 720 или 1080), но email пустой (пользователь не вошел)
        if (quality > 480 && (email == null || email.isEmpty())) {
            // Возвращаем статус 403 (Forbidden) и текст ошибки
            return ResponseEntity.status(403).body("Качество 720p и 1080p доступно только зарегистрированным пользователям.");
        }

        // 2. ИЩЕМ ФИЛЬМ В БАЗЕ
        com.kinostream.backend.Models.Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Видео не найдено"));

        // 3. ВЫБИРАЕМ НУЖНУЮ ССЫЛКУ ИЗ БАЗЫ
        String videoUrl = "";
        if (quality == 480) {
            videoUrl = video.getVideoUrl480();
        } else if (quality == 720) {
            videoUrl = video.getVideoUrl720();
        } else if (quality == 1080) {
            videoUrl = video.getVideoUrl1080();
        }

        // 4. ПРОВЕРЯЕМ, ЗАГРУЖЕН ЛИ ФАЙЛ
        if (videoUrl == null || videoUrl.isEmpty()) {
            return ResponseEntity.status(404).body("Извините, видео в качестве " + quality + "p еще не загружено.");
        }

        // 5. УСПЕХ!
        return ResponseEntity.ok(videoUrl);
    }
    @GetMapping("/api/videos/search")
    @ResponseBody
    public List<Video> searchVideos(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return videoRepository.findAll();
        }
        return videoRepository.findByTitleContainingIgnoreCase(query.trim());
    }
    private final RatingRepository ratingRepository;
}