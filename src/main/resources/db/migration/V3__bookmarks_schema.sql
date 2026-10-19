CREATE TABLE bookmarks (
                           id BIGSERIAL PRIMARY KEY,
                           user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                           video_id BIGINT REFERENCES videos(id) ON DELETE CASCADE,
                           sort_order INT NOT NULL,
    -- Делаем уникальную пару, чтобы нельзя было добавить один фильм дважды:
                           UNIQUE (user_id, video_id)
);