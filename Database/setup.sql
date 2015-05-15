create database if not exists super_pocket;
use super_pocket;

/*user表，用户名和密码*/
create table if not exists user (
    uid int primary key not null auto_increment,
    email varchar(30) not null,
    password varchar(45) not null,
    salt varchar(45) not null
);

create table if not exists post (
    pid int primary key not null auto_increment,
    title varchar(100) not null default '无标题',
    tags varchar(100) not null default '未分类',
    content longtext not null,
    flag int not null default 0,
    head longtext not null,
    uid int not null
);

alter table post add foreign key fk1(uid) references user(uid);

/*插入几个测试用户*/
/*insert into user(email, password, salt) values('test@gmail.com', '123456', '111'), ('love@gmail.com', 'love77', '222');*/
