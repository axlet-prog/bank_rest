--liquibase formatted sql

--changeSet axlet:users_data_1
insert into bank.users(username, password_hash, role)
values ('admin', '{noop}1234', 'ADMIN'),
       ('user',  '{noop}1234', 'USER' );

alter sequence bank.users_s restart with 3;