alter table bookmark
    add column status varchar(16) not null default 'Unread';

insert into bookmark (url) values ('https://www.google.ca');
