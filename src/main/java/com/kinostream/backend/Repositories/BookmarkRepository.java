package com.kinostream.backend.Repositories;

import com.kinostream.backend.Models.Bookmark;
import com.kinostream.backend.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // Получить все закладки юзера, отсортированные по полю sortOrder
    List<Bookmark> findAllByUserOrderBySortOrderAsc(User user);

    // Найти максимальный sort_order у этого юзера (чтобы прикрепить новую закладку в конец)
    @Query("SELECT COALESCE(MAX(b.sortOrder), 0) FROM Bookmark b WHERE b.user = :user")
    Integer findMaxSortOrderByUser(@Param("user") User user);
}