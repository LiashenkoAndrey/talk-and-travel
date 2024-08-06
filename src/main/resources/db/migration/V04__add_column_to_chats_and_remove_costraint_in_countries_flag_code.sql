ALTER TABLE chats add column country_id text references countries(name);
ALTER TABLE countries DROP CONSTRAINT countries_flag_code_key;