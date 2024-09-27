-- Create table for attachments (single table inheritance strategy)
CREATE TABLE attachments
(
    id            BIGSERIAL PRIMARY KEY,
    type          VARCHAR(255) NOT NULL, -- Discriminator for the type of attachment (AUDIO, FILE, IMAGE, VIDEO)
    file_name     VARCHAR(255),
    file_url      VARCHAR(255),          -- Location in S3 bucket
    size          BIGINT,                -- File size in bytes
    mime_type     VARCHAR(255),          -- MIME type (e.g., image/jpeg, video/mp4)
    duration      BIGINT,                -- For Audio and Video duration (optional)
    thumbnail_url VARCHAR(255)           -- For Image and Video thumbnails (optional)
);

-- Add 'attachment_id' column to the existing 'messages' table
ALTER TABLE messages
    ADD COLUMN attachment_id BIGINT UNIQUE;

-- Create foreign key relationship between 'messages' and 'attachments'
ALTER TABLE messages
    ADD CONSTRAINT fk_messages_attachment
        FOREIGN KEY (attachment_id) REFERENCES attachments (id) ON DELETE CASCADE;


