--liquibase formatted sql

--changeset axlet:cards_1

create sequence if not exists bank.cards_s;

-- Таблица для хранения данных о картах
create table bank.cards
(
    card_id               bigint                      not null default nextval('bank.cards_s'::regclass),
    user_id               bigint                      not null,
    card_number_encrypted varchar(255)                not null,
    expiry_date           date                        not null,
    status                varchar(32)                 not null default 'ACTIVE',
    balance               decimal(19, 2)              not null default 0.00,

    created_datetime      timestamp without time zone not null default current_timestamp,
    updated_datetime      timestamp without time zone not null default current_timestamp,
    deleted_datetime      timestamp without time zone not null default current_timestamp,

    constraint cards_pk primary key (card_id),

    constraint cards_user_id_fk foreign key (user_id) references bank.users (user_id) on delete cascade,

    constraint cards_balance_check check (balance >= 0)
);

create index if not exists cards_user_id_idx on bank.cards (user_id);