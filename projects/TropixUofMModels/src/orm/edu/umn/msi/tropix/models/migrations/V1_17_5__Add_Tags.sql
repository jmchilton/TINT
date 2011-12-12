create table TAG (ID varchar(255) not null, TAG_NAME varchar(255));

create table JOIN_TAG_OBJECT (TAG_ID varchar(255) not null, OBJECT_ID varchar(255) not null, primary key (TAG_ID, OBJECT_ID)); 
${if_mysql} ALTER TABLE JOIN_TAG_OBJECT ENGINE = INNODB;

create index FK_TG_OB_TG on JOIN_TAG_OBJECT (TAG_ID);
alter table JOIN_TAG_OBJECT add constraint FK_TG_OB_TG foreign key (TAG_ID) references TAG (ID);
create index FK_TG_OB_OB on JOIN_TAG_OBJECT (OBJECT_ID);
alter table JOIN_TAG_OBJECT add constraint FK_TG_OB_OB foreign key (OBJECT_ID) references TROPIX_OBJECT (ID); 

create table JOIN_TAG_USER (TAG_ID varchar(255) not null, USER_ID varchar(255) not null, primary key (TAG_ID, USER_ID)); 
${if_mysql} ALTER TABLE JOIN_TAG_USER ENGINE = INNODB;

create index FK_TG_UR_TG on JOIN_TAG_USER (TAG_ID);
alter table JOIN_TAG_USER add constraint FK_TG_UR_TG foreign key (TAG_ID) references TAG (ID);
create index FK_TG_UR_UR on JOIN_TAG_USER (USER_ID);
alter table JOIN_TAG_USER add constraint FK_TG_UR_UR foreign key (USER_ID) references USER (ID);

create table JOIN_TAG_GROUP (TAG_ID varchar(255) not null, GROUP_ID varchar(255) not null, primary key (TAG_ID, GROUP_ID)); 
${if_mysql} ALTER TABLE JOIN_TAG_GROUP ENGINE = INNODB;

create index FK_TG_GP_TG on JOIN_TAG_GROUP (TAG_ID);
alter table JOIN_TAG_GROUP add constraint FK_TG_GP_TG foreign key (TAG_ID) references TAG (ID);
create index FK_TG_GP_GP on JOIN_TAG_GROUP (GROUP_ID);
alter table JOIN_TAG_GROUP add constraint FK_TG_GP_GP foreign key (GROUP_ID) references TROPIX_GROUP (ID);

