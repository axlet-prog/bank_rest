--liquibase formatted sql

--changeset axlet:users_roles_1

create sequence if not exists bank.roles_s;

create table if not exists bank.roles
(
    user_role_id     bigint                      not null default nextval('bank.roles_s'::regclass),
    user_id          bigint                      not null,
    role_id          bigint                      not null,

    created_datetime timestamp without time zone not null default current_timestamp,
    updated_datetime timestamp without time zone not null default current_timestamp,

    constraint users_roles_pk primary key (user_role_id),
    constraint user_id_fk foreign key (user_id) references bank.users (user_id) on delete cascade,
    constraint role_id_fk foreign key (user_id) references bank.roles (role_id) on delete cascade
);