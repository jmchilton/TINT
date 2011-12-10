alter table FILE_TYPE add column PARENT_FILE_TYPE_ID varchar(255);
create index FK_FT_FT on FILE_TYPE (PARENT_FILE_TYPE_ID);
alter table FILE_TYPE add constraint FK_FT_FT foreign key (PARENT_FILE_TYPE_ID) references FILE_TYPE (ID);
