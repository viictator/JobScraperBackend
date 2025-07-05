CREATE DATABASE IF NOT EXISTS scrapingtings;
USE scrapingtings;

DROP TABLE IF EXISTS books;

CREATE TABLE books (
                       bookID INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT UNIQUE,
                       bookTitle varchar(250)
);

