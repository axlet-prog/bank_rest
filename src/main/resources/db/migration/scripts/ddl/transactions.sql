--liquibase formatted sql

--changeset axlet:transactions_1

create sequence if not exists bank.transactions_s;

create table if not exists bank.transactions
(

    transaction_id   bigint                      not null default nextval('bank.transactions_s'::regclass),
    card_from_id     bigint                      not null,
    card_to_id       bigint                      not null,
    value            decimal(19, 2)              not null,

    created_datetime timestamp without time zone not null default current_timestamp,

    constraint transactions_pk primary key (transaction_id),

    constraint transactions_card_from_id_fk foreign key (card_from_id) references bank.cards (card_id) on delete cascade,
    constraint transactions_card_to_id_fk foreign key (card_to_id) references bank.cards (card_id) on delete cascade,

    constraint transactions_value_check check (value >= 0)
);

-- create index if not exists transactions_user_id_idx on bank.transactions (user_id);