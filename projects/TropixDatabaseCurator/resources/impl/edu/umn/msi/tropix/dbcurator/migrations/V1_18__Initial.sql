create table change_audit (change_audit_id integer not null auto_increment, reason longtext, change_date datetime, primary key (change_audit_id)); 
create table curation (curation_id integer not null auto_increment, short_name varchar(64) not null, first_run_date datetime, curated_filename varchar(255), title varchar(255), email varchar(32), run_date datetime, deployment_date datetime, notes varchar(255), decoy_regex varchar(200), creation integer, deletion integer, primary key (curation_id)); 
create table curation_data_source (data_source_id integer not null auto_increment, name varchar(50) unique, url varchar(255), is_common char(1), auto_transform integer, primary key (data_source_id)); 
create table curation_header_transform (header_transform_id integer not null auto_increment, name varchar(50), group_pattern varchar(255), substitute_pattern varchar(255), is_common char(1), primary key (header_transform_id));
create table curation_step (step_id integer not null auto_increment, last_run_completion_count integer, primary key (step_id));  
create table curation_step_database_upload (upload_id integer not null, server_path varchar(255), client_path varchar(255), md5_checksum tinyblob, primary key (upload_id));  
create table curation_step_header_filter (header_filter_id integer not null, criteria_string longtext, text_mode varchar(255), match_mode varchar(255), primary key (header_filter_id));  
create table curation_step_header_transform (header_transform_id integer not null, description varchar(255), match_pattern varchar(255), substitution_pattern varchar(255), primary key (header_transform_id));  
create table curation_step_list (curation_id integer not null, step_id integer not null, sort_order integer not null, primary key (curation_id, sort_order)); 
create table curation_step_make_decoy (sequence_manipulation_id integer not null, overwrite_mode bit, manipulator_type integer, primary key (sequence_manipulation_id));  
create table curation_step_manual_inclusion (manual_inclusion_id integer not null, inclusion_header longtext, inclusion_sequence longtext, primary key (manual_inclusion_id));  
create table curation_step_new_db (new_database_id integer not null, url longtext, source_db integer, primary key (new_database_id)); 
create table source_database_archive (source_id integer not null auto_increment, source_url varchar(128), source_path varchar(255), server_creation_date datetime, download_date datetime, primary key (source_id));  

alter table curation  add constraint FK216320B52D806023 foreign key (deletion) references change_audit (change_audit_id);
alter table curation  add constraint FK216320B56925EB34 foreign key (creation) references change_audit (change_audit_id);
alter table curation_data_source  add constraint FKB3C179C692A49BF2 foreign key (auto_transform) references curation_header_transform (header_transform_id);
alter table curation_step_database_upload  add constraint FK38DF9B1C925A14E3 foreign key (upload_id) references curation_step (step_id);
alter table curation_step_header_filter  add constraint FK4EF11901E93020DA foreign key (header_filter_id) references curation_step (step_id);
alter table curation_step_header_transform  add constraint FKFE1545432CE9100A foreign key (header_transform_id) references curation_step (step_id);
alter table curation_step_list  add constraint FKD5163F27D7A16438 foreign key (step_id) references curation_step (step_id);
alter table curation_step_list  add constraint FKD5163F27B74BB9C3 foreign key (curation_id) references curation (curation_id);
alter table curation_step_make_decoy  add constraint FK85AA1BA414917D49 foreign key (sequence_manipulation_id) references curation_step (step_id);
alter table curation_step_manual_inclusion  add constraint FK196424ECC86A3C1 foreign key (manual_inclusion_id) references curation_step (step_id);
alter table curation_step_new_db  add constraint FKEBB5F3C6590926C5 foreign key (source_db) references source_database_archive (source_id);
alter table curation_step_new_db  add constraint FKEBB5F3C6EBC63F0A foreign key (new_database_id) references curation_step (step_id);