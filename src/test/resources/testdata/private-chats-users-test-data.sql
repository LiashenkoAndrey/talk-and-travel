insert into public.users (id, about, password, user_email, user_name, role)
values (4, 'I am Tomas!', '$2a$10$rOPHid1otOfATe6g4fFBDeak.dnJsJRGvScf1.aSQ1NIny4WQkQPm',
        'tomas@gmail.com',
        'Tomas', 'USER');


insert into public.chats(id, name, description, chat_type, creation_date) values
    (10000, 'Alice-Bob', 'Private chat for Alice and Bob', 'PRIVATE', '2024-08-24 10:53:28.757878');

insert into public.user_chats(id, user_id, chat_id) VALUES
                                                        (1, 2, 10000),
                                                        (2, 3, 10000);