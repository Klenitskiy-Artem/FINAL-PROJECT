package com.kinostream.backend.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "videos")
@Data
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "release_year")
    private Integer releaseYear;

    private String country;

    @Column(name = "is_premium_only")
    private boolean isPremiumOnly;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "video_genres",
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();
    @Column(name = "poster_url")
    private String posterUrl;

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
    @Column(name = "video_url_480")
    private String videoUrl480;

    @Column(name = "video_url_720")
    private String videoUrl720;

    @Column(name = "video_url_1080")
    private String videoUrl1080;

    public String getVideoUrl480() { return videoUrl480; }
    public void setVideoUrl480(String videoUrl480) { this.videoUrl480 = videoUrl480; }

    public String getVideoUrl720() { return videoUrl720; }
    public void setVideoUrl720(String videoUrl720) { this.videoUrl720 = videoUrl720; }

    public String getVideoUrl1080() { return videoUrl1080; }
    public void setVideoUrl1080(String videoUrl1080) { this.videoUrl1080 = videoUrl1080; }

    @Column(name = "trailer_url")
    private String trailerUrl;

    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }
}