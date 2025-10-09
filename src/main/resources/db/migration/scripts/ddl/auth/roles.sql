--liquibase formatted sql

--changeset axlet:roles_1

create sequence if not exists bank.roles_s;

create table if not exists bank.roles
(
    role_id          bigint                      not null default nextval('bank.roles_s'::regclass),
    role_name        varchar(255)                not null,
    description      varchar(255)                null,

    created_datetime timestamp without time zone not null default current_timestamp,
    updated_datetime timestamp without time zone not null default current_timestamp,

    constraint roles_pk primary key (role_id),
    constraint roles_role_name_uq unique (role_name)
);