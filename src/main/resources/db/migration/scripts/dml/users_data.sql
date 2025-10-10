--liquibase formatted sql

--changeSet axlet:users_data_1
insert into bank.users(username, password_hash)
values ('admin', '{noop}1234'),
       ('user',  '{noop}1234');

alter sequence bank.roles_s restart with 3;