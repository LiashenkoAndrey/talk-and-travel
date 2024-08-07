ALTER TABLE user_chats
    add column user_country_id BIGINT references user_countries (id);

ALTER TABLE messages
    add column chat_id BIGINT references chats (id);
