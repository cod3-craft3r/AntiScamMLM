create table members(
    sno int primary key auto_increment not null,
    name varchar(20) not null,
    ref1 varchar(20),
    ref2 varchar(20),
    ref3 varchar(20),
    ref4 varchar(20),
    ref5 varchar(20)
);

create table sample(
    name varchar(10) not null,
    age int,
    lastname varchar(10)
);

