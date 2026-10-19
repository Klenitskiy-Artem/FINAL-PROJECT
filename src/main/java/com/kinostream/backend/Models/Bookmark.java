package com.kinostream.backend.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bookmarks")
@Data
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(name = "sort_order")
    private Integer sortOrder;

}