create table tag
(
    id   int generated always as identity primary key,
    name varchar(256) not null
);

insert into tag (name)
values ('video'),
       ('book');

create table bookmark_tag
(
    bookmark_id int,
    tag_id int,
    foreign key (bookmark_id) references bookmark(id),
    foreign key (tag_id) references tag(id),
    primary key (bookmark_id, tag_id)
);
