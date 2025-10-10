--liquibase formatted sql

--changeset axlet:dropAll_1
drop schema if exists bank cascade;
truncate table public.databasechangelog;