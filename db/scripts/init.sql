DROP TABLE IF EXISTS actor;
DROP TABLE IF EXISTS character;

CREATE TABLE IF NOT EXISTS character (
    id SERIAL,
    name VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS actor (
    id SERIAL,
    name VARCHAR(255),
    character_id INT,
    PRIMARY KEY(id),
    CONSTRAINT fk_character FOREIGN KEY (character_id) REFERENCES character(id)
);

CREATE PUBLICATION got_publication FOR TABLE actor, character;
