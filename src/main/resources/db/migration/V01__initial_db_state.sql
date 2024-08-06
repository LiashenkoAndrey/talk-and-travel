create table countries
(
    id        bigserial
        primary key,
    flag_code varchar(255) not null
        constraint uk_cyhi7id84ym95eoi8sh2nf957
            unique
        unique,
    name      varchar(255) not null
        constraint uk_1pyiwrqimi3hnl3vtgsypj5r
            unique
        unique
);

alter table countries
    owner to postgres;

create table users
(
    id         bigserial
        primary key,
    about      varchar(500),
    password   varchar(255) not null,
    role       varchar(255)
        constraint users_role_check
            check ((role)::text = ANY
                   ((ARRAY ['ADMIN'::character varying, 'USER'::character varying])::text[])),
    user_email varchar(255) not null,
    user_name  varchar(16)
);

alter table users
    owner to postgres;

create table avatars
(
    id      bigserial
        primary key,
    content oid,
    user_id bigint
        constraint uk_ci98v14q14wlfsbdsbn1gx0u2
            unique
        unique
        constraint fkdh0goytewcg1geffkf1clp4kh
            references users
);

alter table avatars
    owner to postgres;

create table group_messages
(
    id            bigserial
        primary key,
    content       varchar(1000),
    creation_date timestamp(6) not null,
    country_id    bigint       not null
        constraint fkcy0dbprqmdaqde3w09gt3x8v9
            references countries,
    user_id       bigint       not null
        constraint fkn5qquaksoym7avx54ske9b885
            references users
);

alter table group_messages
    owner to postgres;

create table participants
(
    id      bigserial
        primary key,
    user_id bigint not null
        constraint fkghixrahoj1s8cloinfx8lyeqa
            references users
);

alter table participants
    owner to postgres;

create table participant_countries
(
    participant_id bigint not null
        constraint fkrxvum8hw9u331naotjw7133on
            references participants,
    country_id     bigint not null
        constraint fksaecpxdva984dnw3lkagxwy2o
            references countries
);

alter table participant_countries
    owner to postgres;

create table tokens
(
    id         bigserial
        primary key,
    expired    boolean not null,
    revoked    boolean not null,
    token      varchar(255),
    token_type varchar(255)
        constraint tokens_token_type_check
            check ((token_type)::text = 'BEARER'::text),
    user_id    bigint
        constraint fk2dylsfo39lgjyqml2tbe0b0ss
            references users
);

alter table tokens
    owner to postgres;

