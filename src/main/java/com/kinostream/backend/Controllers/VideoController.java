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

        // 1. Качество 480p доступно абсолютно всем (даже гостям)
        if (quality == 480) {
            Video video = videoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Видео не найдено"));
            if (video.getVideoUrl480() == null || video.getVideoUrl480().isEmpty()) {
                return ResponseEntity.status(404).body("Видео в качестве 480p еще не загружено.");
            }
            return ResponseEntity.ok(video.getVideoUrl480());
        }

        // 2. Для качества выше 480p (720 или 1080) проверяем наличие email
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(403).body("Качество 720p и 1080p доступно только по подписке.");
        }

        // 3. Ищем пользователя в базе
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(403).body("Пользователь не найден.");
        }

        // 4. ПРОВЕРЯЕМ АКТИВНОСТЬ ТРИАЛА ИЛИ ПОДПИСКИ
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        boolean hasActiveTrial = user.getTrialEndDate() != null && user.getTrialEndDate().isAfter(now);
        boolean hasActiveSub = user.getSubscriptionEndDate() != null && user.getSubscriptionEndDate().isAfter(now);

        if (!hasActiveTrial && !hasActiveSub) {
            return ResponseEntity.status(403).body("Срок действия подписки истек. Для просмотра в HD оформите подписку.");
        }

        // 5. ИЩЕМ ФИЛЬМ В БАЗЕ
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Видео не найдено"));

        // 6. ВЫБИРАЕМ ССЫЛКУ ПО КАЧЕСТВУ
        String videoUrl = "";
        if (quality == 720) {
            videoUrl = video.getVideoUrl720();
        } else if (quality == 1080) {
            videoUrl = video.getVideoUrl1080();
        }

        // 7. ПРОВЕРЯЕМ, ЗАГРУЖЕН ЛИ ФАЙЛ
        if (videoUrl == null || videoUrl.isEmpty()) {
            return ResponseEntity.status(404).body("Извините, видео в качестве " + quality + "p еще не загружено.");
        }

        // 8. УСПЕХ!
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