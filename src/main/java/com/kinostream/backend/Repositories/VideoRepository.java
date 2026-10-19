package com.kinostream.backend.Repositories;

import com.kinostream.backend.Models.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {

    // Найти все фильмы/сериалы определенного жанра
    List<Video> findByGenres_Name(String genreName);

    // Найти все видео определенной категории (например, 'Фильм')
    List<Video> findByCategory_Name(String categoryName);

    // Найти все видео определенного года
    List<Video> findByReleaseYear(Integer releaseYear);

    // Получить последние 10 добавленных видео (сортировка по ID в обратном порядке)
    List<Video> findTop10ByOrderByIdDesc();

    List<Video> findByTitleContainingIgnoreCase(String title);

    List<Video> findByCategory_NameIgnoreCase(String categoryName);
}