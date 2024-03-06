create table bookmark
(
    id  int generated always as identity primary key,
    url varchar(2048) not null
)