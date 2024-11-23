
-- drop not null constraint
alter table users
    alter column user_email drop not null;