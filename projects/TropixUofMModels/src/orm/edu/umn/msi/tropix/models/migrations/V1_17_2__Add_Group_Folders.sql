create table JOIN_GROUP_SHARED_FOLDER (GROUP_ID varchar(255) not null, VIRTUAL_FOLDER_ID varchar(255) not null, primary key (GROUP_ID, VIRTUAL_FOLDER_ID)); 
${if_mysql} ALTER TABLE JOIN_GROUP_SHARED_FOLDER ENGINE = INNODB;

create index FK_G_SF_SF on JOIN_GROUP_SHARED_FOLDER (VIRTUAL_FOLDER_ID);
alter table JOIN_GROUP_SHARED_FOLDER add constraint FK_G_SF_SF foreign key (VIRTUAL_FOLDER_ID) references VIRTUAL_FOLDER (OBJECT_ID);
create index FK_G_SF_G on JOIN_GROUP_SHARED_FOLDER (GROUP_ID);
alter table JOIN_GROUP_SHARED_FOLDER add constraint FK_G_SF_G foreign key (GROUP_ID) references TROPIX_GROUP (ID);

