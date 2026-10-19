-- Добавляем колонку для хранения ссылки на постер
ALTER TABLE videos ADD COLUMN poster_url VARCHAR(255);

-- Устанавливаем дефолтную картинку для уже существующих фильмов
UPDATE videos SET poster_url = '/uploads/default-poster.jpg';