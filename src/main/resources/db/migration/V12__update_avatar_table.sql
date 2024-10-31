

delete from avatars;
alter table avatars drop column content;
alter table avatars add column key uuid not null;
