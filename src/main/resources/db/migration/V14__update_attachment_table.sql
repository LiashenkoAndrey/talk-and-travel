

ALTER TABLE public.messages
    DROP CONSTRAINT IF EXISTS fk_messages_attachment;

ALTER TABLE public.messages
    ALTER COLUMN attachment_id TYPE text USING attachment_id::text;

-- Step 2: Drop the primary key constraint on the id column in attachments
ALTER TABLE public.attachments
    DROP CONSTRAINT IF EXISTS attachments_pkey;


ALTER TABLE public.attachments
    ALTER COLUMN id TYPE text USING id::text;

-- Step 4: Recreate the primary key constraint on the id column in attachments
ALTER TABLE public.attachments
    ADD PRIMARY KEY (id);

-- Step 5: Recreate the foreign key constraint on the messages table
ALTER TABLE public.messages
    ADD CONSTRAINT fk_messages_attachment FOREIGN KEY (attachment_id) REFERENCES public.attachments(id) ON DELETE CASCADE;


ALTER TABLE public.messages
    DROP CONSTRAINT fk_messages_attachment,
    ADD CONSTRAINT fk_messages_attachment FOREIGN KEY (attachment_id) REFERENCES public.attachments(id) ON DELETE CASCADE;

ALTER TABLE public.messages
    DROP COLUMN IF EXISTS file_url,
    DROP COLUMN IF EXISTS thumbnail_url;
