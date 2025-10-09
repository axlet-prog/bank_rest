--liquibase formatted sql

--changeset runAlways:true
create schema if not exists bank;
