create database if not exists super_pocket;
use super_pocket;

/*user表，用户名和密码*/
create table if not exists user (
    uid int primary key not null auto_increment,
    email varchar(30) not null,
    password varchar(20) not null
);

/*插入几个测试用户*/
insert into user(email, password) values('test@gmail.com', '123456'), ('love@gmail.com', 'love77');
