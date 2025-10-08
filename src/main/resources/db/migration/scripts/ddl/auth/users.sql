--liquibase formatted sql

--changeset axlet:users_1

create sequence if not exists bank.users_s;

create type bank.user_role as enum ('USER', 'ADMIN');

create table bank.users
(
    user_id          bigint                      not null default nextval('bank.users_s'::regclass),
    username         varchar(255)                not null,
    password_hash    varchar(255)                not null,
    role             varchar(32)                 not null,

    created_datetime timestamp without time zone not null default current_timestamp,
    updated_datetime timestamp without time zone not null default current_timestamp,
    deleted_datetime timestamp without time zone not null default current_timestamp,

    constraint users_pk primary key (user_id),
    constraint users_username_uq unique (username)
);