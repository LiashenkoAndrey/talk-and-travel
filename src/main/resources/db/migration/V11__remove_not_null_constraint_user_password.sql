-- remove not null constraint from users table to add registering from social
alter table users alter column password drop NOT NULL;