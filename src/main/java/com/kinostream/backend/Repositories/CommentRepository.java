package com.kinostream.backend.Repositories;

import com.kinostream.backend.Models.Comment;
import com.kinostream.backend.Models.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Получаем все комментарии к фильму (новые сверху)
    List<Comment> findByVideoOrderByCreatedAtDesc(Video video);
}