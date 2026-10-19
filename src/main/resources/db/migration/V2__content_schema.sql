-- Таблица категорий (Фильм или Сериал)
CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(50) NOT NULL UNIQUE
);

-- Таблица жанров
CREATE TABLE genres (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(50) NOT NULL UNIQUE
);

-- Основная таблица видео-контента
CREATE TABLE videos (
                        id BIGSERIAL PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        description TEXT,
                        release_year INT,
                        country VARCHAR(100),
                        is_premium_only BOOLEAN DEFAULT FALSE,
                        category_id BIGINT REFERENCES categories(id)
);

-- Связь многие-ко-многим для жанров (у одного фильма может быть несколько жанров)
CREATE TABLE video_genres (
                              video_id BIGINT REFERENCES videos(id) ON DELETE CASCADE,
                              genre_id BIGINT REFERENCES genres(id) ON DELETE CASCADE,
                              PRIMARY KEY (video_id, genre_id)
);

-- Заполняем базовые справочники
INSERT INTO categories (name) VALUES ('Фильм'), ('Сериал');
INSERT INTO genres (name) VALUES ('Боевик'), ('Комедия'), ('Драма'), ('Хоррор'), ('Фантастика');
-- Добавляем тестовый фильм для проверки плеера
-- Добавляем разнообразный контент (категории: 1 - Фильм, 2 - Сериал)
INSERT INTO videos (title, description, release_year, country, is_premium_only, category_id) VALUES
                                                                                                 ('Джигурда', 'Описание для проверки', 2026, 'Кавкаазз', false, 1),
                                                                                                 ('Мишаня Круг: не в своём исполнении', 'Бурбон пытается быть певцом, что же из этого выйдет?', 2010, 'Москва', true, 2),
                                                                                                 ('Стройка - дело такое', 'Строитель переработал и пытается попасть на объект.', 2005, 'Неизвестно', false, 1),
                                                                                                 ('Начинающий повар', 'Парень решил стать поваром, но решил спросить другого повара.', 2008, 'Неизвестно', true, 2),
                                                                                                 ('Сова и другие преключения', 'Женщина думала, что увидела сову, но так ли это?', 2011, 'Россия', false, 1),
                                                                                                 ('Боец', 'Парень только вернувшийся с поля боевых действий - наступает тапком на мину.', 2009, 'Неизвестно', true, 1),
                                                                                                 ('Морская пехота', 'Однажды морской пехотинец оказался в России, без транспорта, а выполнить задание - необходимо.', 2003, 'Россия', false, 2),
                                                                                                 ('Ёлки', 'Ёлочка - нравицца', 2007, 'Россия', false, 1);