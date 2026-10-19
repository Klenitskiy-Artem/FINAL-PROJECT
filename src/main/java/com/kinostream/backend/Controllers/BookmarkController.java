package com.kinostream.backend.Controllers;

import com.kinostream.backend.Models.Bookmark;
import com.kinostream.backend.Models.User;
import com.kinostream.backend.Models.Video;
import com.kinostream.backend.Repositories.BookmarkRepository;
import com.kinostream.backend.Repositories.UserRepository;
import com.kinostream.backend.Repositories.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    // 1. Добавить видео в закладки
    @PostMapping("/add")
    public ResponseEntity<?> addBookmark(@RequestParam String email, @RequestParam Long videoId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Юзер не найден"));
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Видео не найдено"));

        // Если в базе еще нет закладок, ставим 0, иначе берем максимум и прибавляем 1
        Integer currentMax = bookmarkRepository.findMaxSortOrderByUser(user);
        int nextOrder = (currentMax == null ? 0 : currentMax) + 1;

        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setVideo(video);
        bookmark.setSortOrder(nextOrder);

        bookmarkRepository.save(bookmark);
        return ResponseEntity.ok("Фильм добавлен в закладки!");
    }

    // 2. Получить закладки (Шаг 5 УЖЕ СДЕЛАН ТУТ! База сама сортирует)
    @GetMapping
    public ResponseEntity<List<Bookmark>> getBookmarks(@RequestParam String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Юзер не найден"));
        return ResponseEntity.ok(bookmarkRepository.findAllByUserOrderBySortOrderAsc(user));
    }

    // 3. Изменить порядок (Drag-and-Drop)
    @PutMapping("/reorder") // <-- Исправили дублирование пути
    public ResponseEntity<?> reorderBookmarks(@RequestParam String email, @RequestBody List<Long> orderedIds) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Пользователь не найден");
        }

        // Проходимся по присланному массиву ID и расставляем им порядковые номера
        for (int i = 0; i < orderedIds.size(); i++) {
            Long bookmarkId = orderedIds.get(i);
            Optional<Bookmark> bookmarkOpt = bookmarkRepository.findById(bookmarkId);

            if (bookmarkOpt.isPresent()) {
                Bookmark bookmark = bookmarkOpt.get();
                // Убедимся, что закладка принадлежит именно этому пользователю
                if (bookmark.getUser().getId().equals(userOpt.get().getId())) {

                    bookmark.setSortOrder(i); // <-- Заменили setDisplayOrder на setSortOrder

                    bookmarkRepository.save(bookmark);
                }
            }
        }

        return ResponseEntity.ok("Порядок сохранен");
    }
}