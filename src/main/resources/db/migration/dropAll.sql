--liquibase formatted sql

--changeset runAlways:true
drop schema if exists bank cascade;
truncate table public.databasechangelog;