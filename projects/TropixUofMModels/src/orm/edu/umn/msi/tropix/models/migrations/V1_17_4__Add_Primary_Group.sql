alter table USER add column PRIMARY_GROUP_ID varchar(255);

create index FK_U_G_PG on USER (PRIMARY_GROUP_ID); 
alter table USER add constraint FK_U_G_PG foreign key (PRIMARY_GROUP_ID) references TROPIX_GROUP (ID);
