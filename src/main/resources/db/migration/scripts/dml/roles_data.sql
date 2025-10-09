--liquibase formatted sql

--changeSet axlet:roles_data_1
insert into bank.roles(role_id, role_name, description)
values (1, 'ADMIN', 'admin'),
       (2, 'USER',  'user' );

alter sequence bank.roles_s restart with 3;