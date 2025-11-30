-- V1: Create database and configure charset
-- NOTE: This requires sufficient privileges (CREATE DATABASE).
-- If your DB user doesn't have these privileges, pre-create the database manually
-- or remove this script and rely on pre-existing database.

CREATE DATABASE IF NOT EXISTS synq
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE synq;

