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
    uid int not null,
    plain longtext not null,
    time datetime not null default '2015-05-01',
    vector text not null
);

/*post tag对应表*/
create table if not exists pt (
    ptid int primary key not null auto_increment,
    pid int not null,
    tag varchar(100) not null,
    uid int not null
);

create table if not exists setting (
    uid int primary key not null,
    method_id int not null
);
/*alter table post add foreign key fk1(uid) references user(uid);
alter table user add index(email);
alter table post add index(uid);*/
alter table pt add foreign key fk2(uid) references user(uid);
alter table pt add foreign key fk3(pid) references post(pid);
