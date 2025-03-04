--INSERT INTO user_tb (email, password, name, role) VALUES ('tngus@pusan.ac.kr', '{bcrypt}$2a$10$8H0OT8wgtALJkig6fmypi.Y7jzI5Y7W9PGgRKqnVeS2cLWGifwHF2', 'tngus', 'ROLE_USER');
/*INSERT INTO roadmap_tb (id, name) VALUES ('1', '로드맵 제목')
INSERT INTO step_tb (id,title) VALUES ('1', '스프링 시큐리티');

INSERT INTO til_tb (id, title, content) VALUES ('1','이것은 제목','이것은 내용');
*/
--drop table if exists reference_tb CASCADE
--drop table if exists roadmap_tb CASCADE
--drop table if exists step_tb CASCADE
drop table if exists til_tb CASCADE;
--drop table if exists user_roadmap_tb CASCADE
--drop table if exists user_step_tb CASCADE
--drop table if exists user_tb CASCADE
--
--create table user_tb (
--    id bigint not null auto_increment,
--    created_date timestamp,
--    updated_date timestamp,
--    email varchar(50) not null,
--    image varchar(255),
--    name varchar(50) not null,
--    password varchar(100) not null,
--    role varchar(255),
--    primary key (id)
--);
--
--create table user_step_tb (
--    id bigint not null auto_increment,
--    is_submit boolean not null,
--    step_id bigint,
--    user_id bigint,
--    primary key (id)
--);
--
--create table user_roadmap_tb (
--    id bigint not null auto_increment,
--    content varchar(255),
--    is_accept boolean,
--    progress integer not null,
--    role varchar(255) not null,
--    roadmap_id bigint,
--    user_id bigint,
--    primary key (id)
--);
--
create table til_tb (
    id bigint not null auto_increment,
    created_date timestamp,
    updated_date timestamp,
    comment_num integer,
    content varchar(255),
    is_personal boolean,
    submit_content varchar(255),
    submit_date timestamp,
    title varchar(255) not null,
    roadmap_id bigint,
    step_id bigint,
    writer_id bigint,
    primary key (id)
);
--
--create table step_tb (
--    id bigint not null auto_increment,
--    description varchar(255),
--    due_date timestamp,
--    title varchar(255) not null,
--    roadmap_id bigint,
--    primary key (id)
--);
--
--create table roadmap_tb (
--    id bigint not null auto_increment,
--    created_date timestamp,
--    updated_date timestamp,
--    category varchar(255) not null,
--    code varchar(255),
--    current_num bigint,
--    description varchar(255),
--    image varchar(255),
--    is_public boolean,
--    is_recruit boolean,
--    name varchar(255) not null,
--    step_num bigint,
--    creator_id bigint,
--    primary key (id)
--);
--
--create table reference_tb (
--    id bigint not null auto_increment,
--    category varchar(255) not null,
--    link varchar(255) not null,
--    step_id bigint,
--    primary key (id)
--);

insert into til_tb (id, created_date, updated_date, comment_num, content, is_personal, roadmap_id, step_id, submit_content, submit_date, title, writer_id) values (default, '2023-09-10', '2023-09-10', 0, '이것은 내용입니다.', true, '1', '5', null, null, '이것은 제목입니다!!', '1');
insert into til_tb (id, created_date, updated_date, comment_num, content, is_personal, roadmap_id, step_id, submit_content, submit_date, title, writer_id) values (default, '2023-10-10', '2023-10-10', 0, '이것은 내용입니다.', true, '1', '6', null, null, '이것은 제목입니다!!!', '1');
insert into til_tb (id, created_date, updated_date, comment_num, content, is_personal, roadmap_id, step_id, submit_content, submit_date, title, writer_id) values (default, '2022-12-10', '2022-12-10', 0, '이것은 내용입니다.', true, '1', '7', null, null, '이것은 제목입니다!!!!', '1');
insert into til_tb (id, created_date, updated_date, comment_num, content, is_personal, roadmap_id, step_id, submit_content, submit_date, title, writer_id) values (default, '2023-01-10', '2023-01-10', 0, '이것은 내용입니다.', true, '1', '8', null, null, '이것은 제목입니다 홍홍', '1');
insert into til_tb (id, created_date, updated_date, comment_num, content, is_personal, roadmap_id, step_id, submit_content, submit_date, title, writer_id) values (default, '2023-02-10', '2023-02-10', 0, '이것은 내용입니다.', true, '1', '9', null, null, '이것은 제목입니다.', '1');