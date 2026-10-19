package com.kinostream.backend.Repositories;

import com.kinostream.backend.Models.Rating;
import com.kinostream.backend.Models.User;
import com.kinostream.backend.Models.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserAndVideo(User user, Video video);

    List<Rating> findByVideo(Video video);

    // Считаем среднее значение для конкретного фильма
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.video = :video")
    Double getAverageRatingByVideo(@Param("video") Video video);
}