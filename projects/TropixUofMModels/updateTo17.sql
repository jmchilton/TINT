create table GALAXY_TOOL (ID varchar(255) not null, NAME varchar(255), DESCRIPTION varchar(255), primary key (ID)); ALTER TABLE GALAXY_TOOL ENGINE = INNODB;
create table GALAXY_TOOL_REVISION (ID varchar(255) not null, REVISION_NUM bigint, XML longtext, GALAXY_TOOL_ID varchar(255), primary key (ID)); ALTER TABLE GALAXY_TOOL_REVISION ENGINE = INNODB;
alter table GALAXY_TOOL_REVISION add index FK25008FB3A4EE179A (GALAXY_TOOL_ID), add constraint FK25008FB3A4EE179A foreign key (GALAXY_TOOL_ID) references GALAXY_TOOL (ID);

create table FILE_TYPE (ID varchar(255) not null, SHORT_NAME varchar(255), EXTENSION varchar(255), primary key (ID)); ALTER TABLE FILE_TYPE ENGINE = INNODB;
alter table FILE add column FILE_TYPE_ID VARCHAR(255);

insert into FILE_TYPE (ID, SHORT_NAME, EXTENSION) VALUES ('93bf2351-1e8b-4b5a-881b-190735014ffd', 'Unknown', '');
insert into FILE_TYPE (ID, SHORT_NAME, EXTENSION) VALUES ('7a65902b-746f-41dc-a50a-23274c9a2ddf', 'MzXML', '.mzxml');
insert into FILE_TYPE (ID, SHORT_NAME, EXTENSION) VALUES ('2a5a8508-d41a-4546-b10e-575fbe2b63dc', 'Thermo Finnigan Raw', '.RAW');
insert into FILE_TYPE (ID, SHORT_NAME, EXTENSION) VALUES ('399e9b42-778e-4066-a27f-b0ddb1c57912', 'Zip', '.zip');
insert into FILE_TYPE (ID, SHORT_NAME, EXTENSION) VALUES ('53036a4e-9b2d-46c4-bf24-e25028fe13c6', 'XML', '.xml');
insert into FILE_TYPE (ID, SHORT_NAME, EXTENSION) VALUES ('03749ed6-569d-4d18-b196-0a19d118d825', 'Zipped OMSSA Output', '.omx.zip');
insert into FILE_TYPE (ID, SHORT_NAME, EXTENSION) VALUES ('a550dc46-ab2d-4040-95c4-4cc995f21ef5', 'FASTA', '.fasta');
insert into FILE_TYPE (ID, SHORT_NAME, EXTENSION) VALUES ('975c3f7c-11e8-451c-bafe-cdaf2baaef98', 'FASTQ', '.fastq');
insert into FILE_TYPE (ID, SHORT_NAME, EXTENSION) VALUES ('64159eb1-4bf3-45d9-8846-147600ceae47', 'Text', '.txt');
insert into FILE_TYPE (ID, SHORT_NAME, EXTENSION) VALUES ('cb51b4fa-15da-4cc8-92fd-e1c99711b432', 'Tabular', '.xls');
insert into FILE_TYPE (ID, SHORT_NAME, EXTENSION) VALUES ('2a008df4-0e67-4b1a-b2d0-76105ff04955', 'Scaffold Report', '.sfd');
insert into FILE_TYPE (ID, SHORT_NAME, EXTENSION) VALUES ('fd1427be-0eb3-44c3-a123-5be33e6d219f', 'Bowtie Index', '.ebwt.zip');
insert into FILE_TYPE (ID, SHORT_NAME, EXTENSION) VALUES ('05edfbc5-5512-4850-848d-88e825b22303', 'Mascot Output', '.dat');

update FILE SET FILE_TYPE_ID = '93bf2351-1e8b-4b5a-881b-190735014ffd' where TYPE like 'Unknown%';
update FILE SET FILE_TYPE_ID = '93bf2351-1e8b-4b5a-881b-190735014ffd' where TYPE is null;
update FILE SET FILE_TYPE_ID = 'cb51b4fa-15da-4cc8-92fd-e1c99711b432' where TYPE = 'iTraq Quantitation (.xls)';
update FILE SET FILE_TYPE_ID = 'cb51b4fa-15da-4cc8-92fd-e1c99711b432' where TYPE = 'Scaffold Report (.xls)';
update FILE SET FILE_TYPE_ID = '53036a4e-9b2d-46c4-bf24-e25028fe13c6' where TYPE = 'OMSSA_ANALYSIS Input';
update FILE SET FILE_TYPE_ID = '53036a4e-9b2d-46c4-bf24-e25028fe13c6' where TYPE = 'iTraq Quantitation Training (.xml)';
update FILE SET FILE_TYPE_ID = '53036a4e-9b2d-46c4-bf24-e25028fe13c6' where TYPE = 'iTraq Quantitation (.xml)';
update FILE SET FILE_TYPE_ID = '53036a4e-9b2d-46c4-bf24-e25028fe13c6' where TYPE = 'X! Tandem Output (.xml)';
update FILE SET FILE_TYPE_ID = '53036a4e-9b2d-46c4-bf24-e25028fe13c6' where TYPE = 'OMSSA Input (.xml)';
update FILE SET FILE_TYPE_ID = '53036a4e-9b2d-46c4-bf24-e25028fe13c6' where TYPE = 'Scaffold Input (.xml)';
update FILE SET FILE_TYPE_ID = '05edfbc5-5512-4850-848d-88e825b22303' where TYPE = 'Mascot Output (.dat)';
update FILE SET FILE_TYPE_ID = '2a5a8508-d41a-4546-b10e-575fbe2b63dc' where TYPE = 'Thermo Finnigan Raw (.RAW)';
update FILE SET FILE_TYPE_ID = '399e9b42-778e-4066-a27f-b0ddb1c57912' where TYPE = 'Sequest Output (.zip)';
update FILE SET FILE_TYPE_ID = '7a65902b-746f-41dc-a50a-23274c9a2ddf' where TYPE = 'MzXML (.mzXML)';
update FILE SET FILE_TYPE_ID = 'a550dc46-ab2d-4040-95c4-4cc995f21ef5' where TYPE = 'FASTA (.fasta)';
update FILE SET FILE_TYPE_ID = '2a008df4-0e67-4b1a-b2d0-76105ff04955' where TYPE = 'Scaffold Output (.sfd)';
update FILE SET FILE_TYPE_ID = '03749ed6-569d-4d18-b196-0a19d118d825' where TYPE = 'Zipped OMSSA Output (.omx.zip)';

alter table FILE add index FK20ED7C3ACB6AF2 (FILE_TYPE_ID), add constraint FK20ED7C3ACB6AF2 foreign key (FILE_TYPE_ID) references FILE_TYPE (ID);
alter table FILE drop column TYPE;
