-- Add new column 'user_country_id' to 'user_chats' table and set it as a foreign key
ALTER TABLE user_chats
    ADD COLUMN user_country_id BIGINT;

ALTER TABLE user_chats
    ADD CONSTRAINT fk_user_country_id
        FOREIGN KEY (user_country_id) REFERENCES user_countries(id);

-- Add new column 'chat_id' to 'messages' table and set it as a foreign key
ALTER TABLE messages
    ADD COLUMN chat_id BIGINT;

ALTER TABLE messages
    ADD CONSTRAINT fk_chat_id
        FOREIGN KEY (chat_id) REFERENCES chats(id);
