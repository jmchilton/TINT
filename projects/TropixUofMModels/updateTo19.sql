alter table FILE drop column FILE_SIZE;

update PROTEOMICS_RUN as p inner join THERMOFINNIGAN_RUN as t on t.PROTEOMICS_RUN_ID = p.RUN_ID set p.SOURCE_ID = t.RAW_FILE_ID;

create table PHYSICAL_FILE (ID varchar(255) not null, SIZE bigint, HASH varchar(255), FILE_ID varchar(255) unique, primary key (ID)); ALTER TABLE PHYSICAL_FILE ENGINE = INNODB;
alter table PHYSICAL_FILE add index FK172DC3E420D5F787 (FILE_ID), add constraint FK172DC3E420D5F787 foreign key (FILE_ID) references FILE (OBJECT_ID);

alter table THERMOFINNIGAN_RUN drop foreign key FK37D96BBD5069AD20;
alter table THERMOFINNIGAN_RUN drop foreign key FK37D96BBD76E0AE70;

drop table THERMOFINNIGAN_RUN;
