create table USER_PREFERENCE_TYPE (ID VARCHAR(255) not null, PREFERENCE_KEY varchar(255) not null, primary key (ID));
${if_mysql} ALTER TABLE USER_PREFERENCE_TYPE ENGINE = INNODB;
create table USER_PREFERENCE (ID varchar(255) not null, USER_ID varchar(255) not null, USER_PREFERENCE_TYPE_ID varchar(255), PREFERENCE_VALUE varchar(255), primary key (ID)); 
${if_mysql} ALTER TABLE USER_PREFERENCE ENGINE = INNODB;

create index FK_UP_UPT on USER_PREFERENCE (USER_PREFERENCE_TYPE_ID);
alter table USER_PREFERENCE add constraint FK_UP_UPT foreign key (ID) references USER_PREFERENCE_TYPE (ID);

create index FK_UP_U on USER_PREFERENCE (USER_ID);
alter table USER_PREFERENCE add constraint FK_UP_U foreign key (ID) references USER (ID);

