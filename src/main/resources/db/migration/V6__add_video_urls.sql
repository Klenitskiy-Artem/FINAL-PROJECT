ALTER TABLE videos ADD COLUMN video_url_480 VARCHAR(255);
ALTER TABLE videos ADD COLUMN video_url_720 VARCHAR(255);
ALTER TABLE videos ADD COLUMN video_url_1080 VARCHAR(255);

-- Для проверки дадим одному из фильмов тестовые ссылки.
-- Можешь потом заменить их на свои локальные файлы, например '/uploads/jigurda_480.mp4'
UPDATE videos SET
                  video_url_480 = 'https://www.w3schools.com/html/mov_bbb.mp4',
                  video_url_720 = 'https://www.w3schools.com/html/mov_bbb.mp4',
                  video_url_1080 = 'https://www.w3schools.com/html/mov_bbb.mp4'
WHERE title = 'Джигурда';