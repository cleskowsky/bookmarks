alter table bookmark
    add column created_at timestamp default CURRENT_TIMESTAMP;
