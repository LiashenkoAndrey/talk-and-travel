-- Add the new user_id column to the avatars table
ALTER TABLE avatars
    ADD COLUMN user_id BIGINT;

-- Create a temporary table to map the old avatar_id to the new user_id
CREATE TEMPORARY TABLE avatar_user_map AS
SELECT u.id AS user_id, u.avatar_id AS avatar_id
FROM users u
WHERE u.avatar_id IS NOT NULL;

-- Update the avatars table with the correct user_id
UPDATE avatars a
SET user_id = aum.user_id
FROM avatar_user_map aum
WHERE a.id = aum.avatar_id;

-- Remove the avatar_id column from the users table
ALTER TABLE users
    DROP COLUMN avatar_id;

-- Optionally, add a foreign key constraint to ensure referential integrity
ALTER TABLE avatars
    ADD CONSTRAINT fk_user
        FOREIGN KEY (user_id) REFERENCES users(id);

-- Drop the temporary table
DROP TABLE avatar_user_map;

-- Optionally, update the database owner if needed
ALTER TABLE avatars OWNER TO postgres;
ALTER TABLE users OWNER TO postgres;
