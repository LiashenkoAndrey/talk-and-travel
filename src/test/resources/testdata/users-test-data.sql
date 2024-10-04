insert into public.users (id, about, password, user_email, user_name, role)
values (2, 'Hello, I am Alice!', '$2a$10$QDyNQbb6B6EyEb4ZLJ6TR.ogaD4mvmwr6BTszgSUCisONGUUYp4KG',
        'alice@mail.com',
        'Alice', 'USER'),
       (3, 'Hello, I am Bob!', '$2a$10$rOPHid1otOfATe6g4fFBDeak.dnJsJRGvScf1.aSQ1NIny4WQkQPm',
        'bob@mail.com',
        'Bob', 'USER');

