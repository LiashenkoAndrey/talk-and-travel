
-- Add 'last_logged_on' field to 'users' table
ALTER TABLE public.users
    ADD COLUMN last_logged_on timestamp;