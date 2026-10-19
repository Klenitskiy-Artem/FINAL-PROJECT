-- Таблица ролей
CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE
);

-- Основная таблица пользователей
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       trial_end_date TIMESTAMP,
                       subscription_end_date TIMESTAMP,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица связи многие-ко-многим (типы должны быть BIGINT, чтобы совпадать с ID)
CREATE TABLE user_roles (
                            user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                            role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
                            PRIMARY KEY (user_id, role_id)
);

-- Заполним таблицу ролей базовыми значениями
INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');