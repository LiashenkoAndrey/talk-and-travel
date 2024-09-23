
insert into public.user_chats(id, user_id, chat_id)
VALUES (101,
        2, -- Alice
        1); -- Aruba

insert into public.messages(id, content, creation_date, sender_id, chat_id, type)
VALUES (1001,
        'content1',
        '2024-08-24 10:33:28.757878',
        2, -- Alice
        1, -- Aruba
        'TEXT');
