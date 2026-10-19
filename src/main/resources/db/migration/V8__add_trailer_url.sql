ALTER TABLE videos ADD COLUMN trailer_url VARCHAR(255);

-- Добавляем тестовый трейлер для всех существующих фильмов
UPDATE videos SET trailer_url = 'https://www.w3schools.com/html/mov_bbb.mp4';