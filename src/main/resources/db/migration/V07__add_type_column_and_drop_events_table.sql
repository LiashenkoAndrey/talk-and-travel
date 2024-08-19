-- Add the 'type' column to the 'messages' table
ALTER TABLE messages
    ADD COLUMN type VARCHAR(255);

-- Drop the 'events' table
DROP TABLE IF EXISTS events;
