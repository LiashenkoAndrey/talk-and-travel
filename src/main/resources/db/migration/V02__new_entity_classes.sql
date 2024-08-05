-- Drop existing foreign key constraints
ALTER TABLE group_messages
    DROP CONSTRAINT fkcy0dbprqmdaqde3w09gt3x8v9;
ALTER TABLE group_messages
    DROP CONSTRAINT fkn5qquaksoym7avx54ske9b885;
ALTER TABLE participants
    DROP CONSTRAINT fkghixrahoj1s8cloinfx8lyeqa;
ALTER TABLE participant_countries
    DROP CONSTRAINT fkrxvum8hw9u331naotjw7133on;
ALTER TABLE participant_countries
    DROP CONSTRAINT fksaecpxdva984dnw3lkagxwy2o;
ALTER TABLE tokens
    DROP CONSTRAINT fk2dylsfo39lgjyqml2tbe0b0ss;

-- Rename existing tables
ALTER TABLE users
    RENAME TO old_users;
ALTER TABLE avatars
    RENAME TO old_avatars;
ALTER TABLE group_messages
    RENAME TO old_group_messages;
ALTER TABLE participants
    RENAME TO old_participants;
ALTER TABLE participant_countries
    RENAME TO old_participant_countries;
ALTER TABLE tokens
    RENAME TO old_tokens;
ALTER TABLE countries
    RENAME TO old_countries;

-- Create new tables according to the new schema
CREATE TABLE countries
(
    name      VARCHAR(255) PRIMARY KEY,
    flag_code VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE avatars
(
    id      BIGSERIAL PRIMARY KEY,
    content OID
);

CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    about      VARCHAR(500),
    password   VARCHAR(255) NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    user_name  VARCHAR(16),
    role       VARCHAR(255),
    avatar_id  BIGINT UNIQUE,
    CONSTRAINT fk_avatar FOREIGN KEY (avatar_id) REFERENCES avatars (id)
);

CREATE TABLE chats
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255),
    description   VARCHAR(500),
    chat_type     VARCHAR(255) CHECK (chat_type IN ('PRIVATE', 'GROUP')),
    creation_date TIMESTAMP(6) NOT NULL
);

CREATE TABLE user_countries
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT       NOT NULL REFERENCES users (id),
    country_name VARCHAR(255) NOT NULL REFERENCES countries (name)
);

CREATE TABLE user_chats
(
    id                   BIGSERIAL PRIMARY KEY,
    user_id              BIGINT NOT NULL REFERENCES users (id),
    chat_id              BIGINT NOT NULL REFERENCES chats (id),
    last_read_message_id BIGINT
);

CREATE TABLE messages
(
    id                 BIGSERIAL PRIMARY KEY,
    content            VARCHAR(1000),
    creation_date      TIMESTAMP(6) NOT NULL,
    sender_id          BIGINT       NOT NULL REFERENCES users (id),
    replied_message_id BIGINT REFERENCES messages (id)
);

CREATE TABLE events
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES users (id),
    chat_id    BIGINT       NOT NULL REFERENCES chats (id),
    event_type VARCHAR(255) CHECK (event_type IN ('JOIN', 'LEAVE', 'TYPING', 'STOPPED_TYPING')),
    event_time TIMESTAMP(6) NOT NULL
);

CREATE TABLE tokens
(
    id         BIGSERIAL PRIMARY KEY,
    expired    BOOLEAN NOT NULL,
    revoked    BOOLEAN NOT NULL,
    token      VARCHAR(255),
    token_type VARCHAR(255) CHECK (token_type = 'BEARER'),
    user_id    BIGINT REFERENCES users (id)
);

-- Migrate data from old tables to new tables (basic example, adjust as needed)
INSERT INTO countries (name, flag_code)
SELECT name, flag_code
FROM old_countries;

INSERT INTO avatars (id, content)
SELECT id, content
FROM old_avatars;

INSERT INTO users (about, password, user_email, user_name, role, avatar_id)
SELECT about,
       password,
       user_email,
       user_name,
       role,
       (SELECT id FROM old_avatars WHERE old_avatars.user_id = old_users.id)
FROM old_users;


INSERT INTO chats (name, description, chat_type, creation_date)
SELECT DISTINCT 'default', 'default', 'PRIVATE', CURRENT_TIMESTAMP
FROM old_group_messages;

-- Note: Data migration for more complex relationships and additional tables is not fully covered here

-- Drop old tables after migration
DROP TABLE old_avatars;
DROP TABLE old_group_messages;
DROP TABLE old_participants;
DROP TABLE old_participant_countries;
DROP TABLE old_tokens;
DROP TABLE old_countries;
DROP TABLE old_users;

-- Add missing indexes or constraints if needed

-- Optionally, update the database owner if needed
ALTER TABLE countries
    OWNER TO postgres;
ALTER TABLE users
    OWNER TO postgres;
ALTER TABLE avatars
    OWNER TO postgres;
ALTER TABLE chats
    OWNER TO postgres;
ALTER TABLE user_countries
    OWNER TO postgres;
ALTER TABLE user_chats
    OWNER TO postgres;
ALTER TABLE messages
    OWNER TO postgres;
ALTER TABLE events
    OWNER TO postgres;
ALTER TABLE tokens
    OWNER TO postgres;
