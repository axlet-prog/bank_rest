--liquibase formatted sql

--changeSet axlet:users_data_1
insert into bank.users_roles(user_id, role_id)
values (1, 1),
       (1, 2),
       (2, 2);

alter sequence bank.roles_s restart with 4;