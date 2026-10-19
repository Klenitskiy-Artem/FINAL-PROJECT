package com.kinostream.backend.Repositories;

import com.kinostream.backend.Models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}