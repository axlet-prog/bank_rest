--liquibase formatted sql

--changeset axlet:refresh_tokens_1

create sequence if not exists bank.refresh_tokens_s;

create table if not exists bank.refresh_tokens
(
    refresh_token_id bigint                      not null default nextval('bank.refresh_tokens_s'::regclass),
    user_id          bigint                      not null,
    token_hash       varchar(255)                not null,
    expiry_date      timestamp without time zone not null,
    created_datetime timestamp without time zone not null default current_timestamp,

    constraint refresh_tokens_pk primary key (refresh_token_id),
    constraint refresh_tokens_user_id_fk foreign key (user_id) references bank.users (user_id) on delete cascade
);

create index if not exists refresh_tokens_user_id_idx on bank.refresh_tokens (user_id);
create index if not exists refresh_tokens_token_hash_idx on bank.refresh_tokens (token_hash);