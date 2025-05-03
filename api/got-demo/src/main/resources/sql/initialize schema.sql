CREATE TABLE IF NOT EXISTS actor (
    id SERIAL,
    name VARCHAR(255),
    character_id INT
);

CREATE TABLE IF NOT EXISTS character (
    id SERIAL,
    name VARCHAR(255)
);
