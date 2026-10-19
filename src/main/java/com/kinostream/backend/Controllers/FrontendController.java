package com.kinostream.backend.Controllers;

import com.kinostream.backend.Models.Video;
import com.kinostream.backend.Repositories.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class FrontendController {

    private final VideoRepository videoRepository;

    @GetMapping("/")
    public String homePage(@RequestParam(required = false) String category, Model model) {
        if (category != null && !category.trim().isEmpty()) {
            String cleanCategory = category.trim();
            // Фильтруем видео по категории ("Фильм" или "Сериал")
            model.addAttribute("videos", videoRepository.findByCategory_NameIgnoreCase(cleanCategory));
            model.addAttribute("currentCategory", cleanCategory);

            // Устанавливаем заголовок на странице
            if (cleanCategory.equalsIgnoreCase("Фильм")) {
                model.addAttribute("pageTitle", "Фильмы");
            } else if (cleanCategory.equalsIgnoreCase("Сериал")) {
                model.addAttribute("pageTitle", "Сериалы");
            } else {
                model.addAttribute("pageTitle", cleanCategory);
            }
        } else {
            // Если перешли просто на "/" (клик по KinoStream) — отдаем все видео и заголовок "Новинки"
            model.addAttribute("videos", videoRepository.findAll());
            model.addAttribute("currentCategory", "ALL");
            model.addAttribute("pageTitle", "Новинки");
        }

        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/video/{id}")
    public String videoPage(@PathVariable Long id, Model model) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Видео не найдено"));

        model.addAttribute("video", video);
        return "video";
    }

    @GetMapping("/bookmarks")
    public String bookmarksPage() {
        return "bookmarks";
    }

    @GetMapping("/profile")
    public String profilePage() {
        return "profile";
    }

    @GetMapping("/subscription")
    public String subscriptionPage() {
        return "subscription";
    }
}