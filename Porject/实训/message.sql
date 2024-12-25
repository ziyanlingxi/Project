create table 实训.message
(
    id        int auto_increment
        primary key,
    from_user varchar(200) null,
    to_user   varchar(200) null,
    message   text         null
);

