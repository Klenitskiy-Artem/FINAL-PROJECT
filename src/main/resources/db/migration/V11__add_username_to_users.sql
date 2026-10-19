ALTER TABLE users ADD COLUMN username VARCHAR(255);

UPDATE users SET username = email WHERE username IS NULL;