
alter table FILE add column FILE_SIZE bigint;
alter table PROTEOMICS_RUN add column SOURCE_ID varchar(255) unique;
alter table PROTEOMICS_RUN add index FK9A81A809EE908C88 (SOURCE_ID), add constraint FK9A81A809EE908C88 foreign key (SOURCE_ID) references FILE (OBJECT_ID);
