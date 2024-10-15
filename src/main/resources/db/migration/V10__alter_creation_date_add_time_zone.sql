
ALTER TABLE public.messages
    ALTER COLUMN creation_date SET DATA TYPE TIMESTAMP WITH TIME ZONE;

ALTER TABLE public.chats
    ALTER COLUMN creation_date SET DATA TYPE TIMESTAMP WITH TIME ZONE;

ALTER TABLE public.users
    ALTER COLUMN last_logged_on SET DATA TYPE TIMESTAMP WITH TIME ZONE;

UPDATE public.messages
SET creation_date = creation_date AT TIME ZONE 'UTC';

UPDATE public.chats
SET creation_date = creation_date AT TIME ZONE 'UTC';

UPDATE public.users
SET last_logged_on = last_logged_on AT TIME ZONE 'UTC';