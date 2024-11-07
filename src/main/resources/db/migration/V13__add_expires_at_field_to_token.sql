
alter table tokens
    add column expires_at timestamp;

ALTER TABLE public.tokens
    DROP CONSTRAINT tokens_token_type_check1,
    ADD CONSTRAINT tokens_token_type_check1 CHECK (token_type IN ('BEARER', 'PASSWORD_RECOVERY'));
