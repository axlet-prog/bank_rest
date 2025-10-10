--liquibase formatted sql

--changeset axlet:schemas_1
create schema if not exists bank;
