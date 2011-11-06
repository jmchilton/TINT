${if_mysql} SET storage_engine=INNODB;

create table ActivityContext (
id varchar(255) not null ${auto_increment},
credentialStr clob,
primary key (id)
);

create table ActivityDependency (
id varchar(255) not null ${auto_increment},
consumerProperty varchar(255),
consumesIndex integer not null,
index integer not null,
key varchar(255),
producerProperty varchar(255),
ACTIVITY_ID varchar(255) not null,
primary key (id)
);

create table ActivityDescription (
id varchar(255) not null ${auto_increment},
activityStatus integer,
jobDescription_id varchar(255),
primary key (id)
);

create table ActivityDescription_ActivityDependency (
ActivityDescription_id varchar(255) not null,
dependencies_id varchar(255) not null,
primary key (ActivityDescription_id, dependencies_id),
unique (dependencies_id)
);

create table CONTEXT_ACTIVITY (
CONTEXT_ID varchar(255) not null,
ACTIVITY_ID varchar(255) not null,
primary key (CONTEXT_ID, ACTIVITY_ID),
unique (ACTIVITY_ID)
);

create table CommitObjectDescription (
destinationId varchar(255),
objectId varchar(255),
id varchar(255) not null,
primary key (id)
);

create table CreateBowtieAnalysisDescription (
bowtieIndexId varchar(255),
outputFileId varchar(255),
id varchar(255) not null,
databaseIds_id varchar(255),
primary key (id)
);

create table CreateBowtieIndexDescription (
indexFileId varchar(255),
id varchar(255) not null,
primary key (id)
);

create table CreateDatabaseDescription (
databaseFileId varchar(255),
databaseType varchar(255),
id varchar(255) not null,
primary key (id)
);

create table CreateFolderDescription (
id varchar(255) not null,
primary key (id)
);

create table CreateITraqQuantitationAnalysisDescription (
outputFileId varchar(255),
reportFileId varchar(255),
trainingId varchar(255),
id varchar(255) not null,
runIdList_id varchar(255),
primary key (id)
);

create table CreateITraqQuantitationTrainingDescription (
outputFileId varchar(255),
reportFileId varchar(255),
id varchar(255) not null,
runIdList_id varchar(255),
primary key (id)
);

create table CreateIdPickerParametersDescription (
id varchar(255) not null,
primary key (id)
);

create table CreateIdentificationAnalysisDescription (
analysisFileId varchar(255),
analysisType varchar(255),
databaseId varchar(255),
parametersId varchar(255),
runId varchar(255),
id varchar(255) not null,
primary key (id)
);

create table CreateIdentificationParametersDescription (
parameterType varchar(255),
parametersId varchar(255),
storageServiceUrl varchar(255),
id varchar(255) not null,
parameters_id varchar(255),
primary key (id)
);

create table CreateMergedIdentificationParametersDescription (
driverFileId varchar(255),
storageServiceUrl varchar(255),
id varchar(255) not null,
databaseIds_id varchar(255),
identificationAnalysisIds_id varchar(255),
parameterSet_id varchar(255),
primary key (id)
);

create table CreateMergedIdentificationParametersDescription_ScaffoldSample (
CreateMergedIdentificationParametersDescription_id varchar(255) not null,
scaffoldSamples_id varchar(255) not null,
id integer not null,
primary key (CreateMergedIdentificationParametersDescription_id, id),
unique (scaffoldSamples_id)
);

create table CreateProteomicsRunDescription (
mzxmlId varchar(255),
sampleId varchar(255),
sourceId varchar(255),
id varchar(255) not null,
primary key (id)
);

create table CreateScaffoldAnalysisDescription (
driverFileId varchar(255),
outputFileId varchar(255),
scaffoldVersion varchar(255),
id varchar(255) not null,
identificationIds_id varchar(255),
primary key (id)
);

create table CreateScaffoldDriverDescription (
scaffoldVersion varchar(255),
id varchar(255) not null,
primary key (id)
);

create table CreateTissueSampleDescription (
id varchar(255) not null,
primary key (id)
);

create table CreateTropixFileDescription (
extension varchar(255),
fileId varchar(255),
storageServiceUrl varchar(255),
id varchar(255) not null,
primary key (id)
);

create table Id (
id varchar(255) not null ${auto_increment},
index integer not null,
value varchar(255),
idList_id varchar(255),
primary key (id)
);

create table IdList (
id varchar(255) not null ${auto_increment},
nextIndex integer,
primary key (id)
);

create table IdList_Id (
IdList_id varchar(255) not null,
ids_id varchar(255) not null,
unique (ids_id)
);

create table JobDescription (
id varchar(255) not null ${auto_increment},
complete bit not null,
name varchar(4096),
primary key (id)
);

create table MergeScaffoldSamplesDescription (
mudpit bit not null,
produceMultipleSamples bit not null,
id varchar(255) not null,
identificationIds_id varchar(255),
names_id varchar(255),
primary key (id)
);

create table MergeScaffoldSamplesDescription_ScaffoldSample (
MergeScaffoldSamplesDescription_id varchar(255) not null,
samples_id varchar(255) not null,
id integer not null,
primary key (MergeScaffoldSamplesDescription_id, id),
unique (samples_id)
);

create table PollJobDescription (
serviceUrl varchar(255),
storageServiceUrl varchar(255),
ticket varchar(255),
id varchar(255) not null,
fileIds_id varchar(255),
primary key (id)
);

create table ScaffoldQuantativeSample (
id varchar(255) not null ${auto_increment},
category varchar(255),
description varchar(255),
name varchar(255),
prim bit,
reporter varchar(255),
primary key (id)
);

create table ScaffoldSample (
id varchar(255) not null ${auto_increment},
analyzeAsMudpit bit not null,
category varchar(255),
quantitativeModelPurityCorrection varchar(255),
quantitativeModelType varchar(255),
sampleName varchar(255),
identificationAnalysisIds_id varchar(255),
primary key (id)
);

create table ScaffoldSample_ScaffoldQuantativeSample (
ScaffoldSample_id varchar(255) not null,
quantitativeSamples_id varchar(255) not null,
id integer not null,
primary key (ScaffoldSample_id, id),
unique (quantitativeSamples_id)
);

create table StringParameter (
id varchar(255) not null ${auto_increment},
key varchar(255),
value varchar(4096),
parameterSet_id varchar(255),
primary key (id)
);

create table StringParameterSet (
id varchar(255) not null ${auto_increment},
primary key (id)
);

create table StringParameterSet_StringParameter (
StringParameterSet_id varchar(255) not null,
parameters_id varchar(255) not null,
primary key (StringParameterSet_id, parameters_id),
unique (parameters_id)
);

create table SubmitBowtieAnalysisDescription (
indexId varchar(255),
inputFromat varchar(255),
inputType varchar(255),
id varchar(255) not null,
databaseIds_id varchar(255),
parameterSet_id varchar(255),
primary key (id)
);

create table SubmitGalaxyDescription (
galaxyToolId varchar(255),
id varchar(255) not null,
persistentRootInput_id varchar(255),
primary key (id)
);

create table SubmitGalaxyDescription$PersistentInput (
id varchar(255) not null ${auto_increment},
name varchar(255),
value varchar(4096),
input_id varchar(255),
rootInput_id varchar(255),
primary key (id)
);

create table SubmitGalaxyDescription$PersistentRootInput (
id varchar(255) not null ${auto_increment},
primary key (id)
);

create table SubmitITraqQuantitationAnalysisDescription (
quantificationType varchar(255),
reportFileId varchar(255),
trainingId varchar(255),
id varchar(255) not null,
runIdList_id varchar(255),
primary key (id)
);

create table SubmitITraqQuantitationTrainingDescription (
quantificationType varchar(255),
reportFileId varchar(255),
id varchar(255) not null,
parameterSet_id varchar(255),
runIdList_id varchar(255),
primary key (id)
);

create table SubmitIdPickerAnalysisDescription (
id varchar(255) not null,
primary key (id)
);

create table SubmitIdentificationAnalysisDescription (
databaseId varchar(255),
parameterType varchar(255),
parametersId varchar(255),
runId varchar(255),
id varchar(255) not null,
primary key (id)
);

create table SubmitJobDescription (
serviceUrl varchar(255),
ticket varchar(255),
id varchar(255) not null,
primary key (id)
);

create table SubmitMergedIdentificationAnalysisDescription (
driverFileId varchar(255),
id varchar(255) not null,
databaseIds_id varchar(255),
identificationIds_id varchar(255),
primary key (id)
);

create table SubmitProteomicsConvertDescription (
inputFileId varchar(255),
inputFormat varchar(255),
inputName varchar(255),
outputFormat varchar(255),
id varchar(255) not null,
primary key (id)
);

create table SubmitScaffoldAnalysisDescription (
id varchar(255) not null,
primary key (id)
);

create table SubmitThermofinniganRunJobDescription (
rawFileBaseName varchar(255),
rawFileId varchar(255),
id varchar(255) not null,
primary key (id)
);

create table TropixObjectDescription (
committed bit not null,
description varchar(4096),
destinationId varchar(255),
name varchar(255),
objectId varchar(255),
id varchar(255) not null,
primary key (id)
);

create table UploadFileDescription (
fileId varchar(255),
inputFilePath varchar(255),
storageServiceUrl varchar(255),
id varchar(255) not null,
primary key (id)
);

alter table ActivityDependency 
add constraint FK6DA317DAA24B304F 
foreign key (ACTIVITY_ID) 
references ActivityDescription;

alter table ActivityDescription 
add constraint FK1089D14DB1937843 
foreign key (jobDescription_id) 
references JobDescription;

alter table ActivityDescription_ActivityDependency 
add constraint FK3465680C7B654911 
foreign key (ActivityDescription_id) 
references ActivityDescription;

alter table ActivityDescription_ActivityDependency 
add constraint FK3465680C1E40EE34 
foreign key (dependencies_id) 
references ActivityDependency;

alter table CONTEXT_ACTIVITY 
add constraint FK1363A3BFDB64343D 
foreign key (CONTEXT_ID) 
references ActivityContext;

alter table CONTEXT_ACTIVITY 
add constraint FK1363A3BFA24B304F 
foreign key (ACTIVITY_ID) 
references ActivityDescription;

alter table CommitObjectDescription 
add constraint FK6E62FEA6D8F7CD1F 
foreign key (id) 
references ActivityDescription;

alter table CreateBowtieAnalysisDescription 
add constraint FKFABAA95EBF781AC1 
foreign key (id) 
references TropixObjectDescription;

alter table CreateBowtieAnalysisDescription 
add constraint FKFABAA95ED133159F 
foreign key (databaseIds_id) 
references IdList;

alter table CreateBowtieIndexDescription 
add constraint FK244E56ACBF781AC1 
foreign key (id) 
references TropixObjectDescription;

alter table CreateDatabaseDescription 
add constraint FK6F1B7CC5BF781AC1 
foreign key (id) 
references TropixObjectDescription;

alter table CreateFolderDescription 
add constraint FK5147ACF2BF781AC1 
foreign key (id) 
references TropixObjectDescription;

alter table CreateITraqQuantitationAnalysisDescription 
add constraint FK6C835EFEBF781AC1 
foreign key (id) 
references TropixObjectDescription;

alter table CreateITraqQuantitationAnalysisDescription 
add constraint FK6C835EFE3BE82118 
foreign key (runIdList_id) 
references IdList;

alter table CreateITraqQuantitationTrainingDescription 
add constraint FKBADE7E40BF781AC1 
foreign key (id) 
references TropixObjectDescription;

alter table CreateITraqQuantitationTrainingDescription 
add constraint FKBADE7E403BE82118 
foreign key (runIdList_id) 
references IdList;

alter table CreateIdPickerParametersDescription 
add constraint FKDE1109ED6CC93CAE 
foreign key (id) 
references CreateMergedIdentificationParametersDescription;

alter table CreateIdentificationAnalysisDescription 
add constraint FK2E884076BF781AC1 
foreign key (id) 
references TropixObjectDescription;

alter table CreateIdentificationParametersDescription 
add constraint FK97E1F728BF781AC1 
foreign key (id) 
references TropixObjectDescription;

alter table CreateIdentificationParametersDescription 
add constraint FK97E1F7289334B0C3 
foreign key (parameters_id) 
references StringParameterSet;

alter table CreateMergedIdentificationParametersDescription 
add constraint FK8B4FE95CF9309434 
foreign key (parameterSet_id) 
references StringParameterSet;

alter table CreateMergedIdentificationParametersDescription 
add constraint FK8B4FE95CD8F7CD1F 
foreign key (id) 
references ActivityDescription;

alter table CreateMergedIdentificationParametersDescription 
add constraint FK8B4FE95C7883542E 
foreign key (identificationAnalysisIds_id) 
references IdList;

alter table CreateMergedIdentificationParametersDescription 
add constraint FK8B4FE95CD133159F 
foreign key (databaseIds_id) 
references IdList;

alter table CreateMergedIdentificationParametersDescription_ScaffoldSample 
add constraint FK1CA58EA35BCAC5D0 
foreign key (scaffoldSamples_id) 
references ScaffoldSample;

alter table CreateMergedIdentificationParametersDescription_ScaffoldSample 
add constraint FK1CA58EA3CE307DD1 
foreign key (CreateMergedIdentificationParametersDescription_id) 
references CreateMergedIdentificationParametersDescription;

alter table CreateProteomicsRunDescription 
add constraint FKB4B7A3EABF781AC1 
foreign key (id) 
references TropixObjectDescription;

alter table CreateScaffoldAnalysisDescription 
add constraint FKE8F9B8EEBF781AC1 
foreign key (id) 
references TropixObjectDescription;

alter table CreateScaffoldAnalysisDescription 
add constraint FKE8F9B8EE31AD8452 
foreign key (identificationIds_id) 
references IdList;

alter table CreateScaffoldDriverDescription 
add constraint FK670ECFA26CC93CAE 
foreign key (id) 
references CreateMergedIdentificationParametersDescription;

alter table CreateTissueSampleDescription 
add constraint FKCF9168D1BF781AC1 
foreign key (id) 
references TropixObjectDescription;

alter table CreateTropixFileDescription 
add constraint FK49D448F6BF781AC1 
foreign key (id) 
references TropixObjectDescription;

alter table Id 
add constraint FK93BCAB82F43 
foreign key (idList_id) 
references IdList;

alter table IdList_Id 
add constraint FK498A56C1A22599A6 
foreign key (ids_id) 
references Id;

alter table IdList_Id 
add constraint FK498A56C1CAB82F43 
foreign key (IdList_id) 
references IdList;

alter table MergeScaffoldSamplesDescription 
add constraint FKD32D1901D8F7CD1F 
foreign key (id) 
references ActivityDescription;

alter table MergeScaffoldSamplesDescription 
add constraint FKD32D190131AD8452 
foreign key (identificationIds_id) 
references IdList;

alter table MergeScaffoldSamplesDescription 
add constraint FKD32D1901C99B81D4 
foreign key (names_id) 
references IdList;

alter table MergeScaffoldSamplesDescription_ScaffoldSample 
add constraint FKA9E8805EC56A315A 
foreign key (samples_id) 
references ScaffoldSample;

alter table MergeScaffoldSamplesDescription_ScaffoldSample 
add constraint FKA9E8805E19AED251 
foreign key (MergeScaffoldSamplesDescription_id) 
references MergeScaffoldSamplesDescription;

alter table PollJobDescription 
add constraint FK1CE4EDBED8F7CD1F 
foreign key (id) 
references ActivityDescription;

alter table PollJobDescription 
add constraint FK1CE4EDBEEBE534E0 
foreign key (fileIds_id) 
references IdList;

alter table ScaffoldSample 
add constraint FK640C6D607883542E 
foreign key (identificationAnalysisIds_id) 
references IdList;

alter table ScaffoldSample_ScaffoldQuantativeSample 
add constraint FKD1E3D2E11CCB10E3 
foreign key (ScaffoldSample_id) 
references ScaffoldSample;

alter table ScaffoldSample_ScaffoldQuantativeSample 
add constraint FKD1E3D2E18567BBB3 
foreign key (quantitativeSamples_id) 
references ScaffoldQuantativeSample;

alter table StringParameter 
add constraint FK89AAE78F9309434 
foreign key (parameterSet_id) 
references StringParameterSet;

alter table StringParameterSet_StringParameter 
add constraint FK7379303379EF003 
foreign key (StringParameterSet_id) 
references StringParameterSet;

alter table StringParameterSet_StringParameter 
add constraint FK7379303B233003F 
foreign key (parameters_id) 
references StringParameter;

alter table SubmitBowtieAnalysisDescription 
add constraint FKBE6A01C2F9309434 
foreign key (parameterSet_id) 
references StringParameterSet;

alter table SubmitBowtieAnalysisDescription 
add constraint FKBE6A01C2D59FF39B 
foreign key (id) 
references SubmitJobDescription;

alter table SubmitBowtieAnalysisDescription 
add constraint FKBE6A01C2D133159F 
foreign key (databaseIds_id) 
references IdList;

alter table SubmitGalaxyDescription 
add constraint FK2AA30974D59FF39B 
foreign key (id) 
references SubmitJobDescription;

alter table SubmitGalaxyDescription 
add constraint FK2AA30974C0A44E41 
foreign key (persistentRootInput_id) 
references SubmitGalaxyDescription$PersistentRootInput;

alter table SubmitGalaxyDescription$PersistentInput 
add constraint FK1DE0EC23BDB4EA2A 
foreign key (rootInput_id) 
references SubmitGalaxyDescription$PersistentRootInput;

alter table SubmitGalaxyDescription$PersistentInput 
add constraint FK1DE0EC23BF71E70A 
foreign key (input_id) 
references SubmitGalaxyDescription$PersistentInput;

alter table SubmitITraqQuantitationAnalysisDescription 
add constraint FKE7B1A01AD59FF39B 
foreign key (id) 
references SubmitJobDescription;

alter table SubmitITraqQuantitationAnalysisDescription 
add constraint FKE7B1A01A3BE82118 
foreign key (runIdList_id) 
references IdList;

alter table SubmitITraqQuantitationTrainingDescription 
add constraint FK360CBF5CF9309434 
foreign key (parameterSet_id) 
references StringParameterSet;

alter table SubmitITraqQuantitationTrainingDescription 
add constraint FK360CBF5CD59FF39B 
foreign key (id) 
references SubmitJobDescription;

alter table SubmitITraqQuantitationTrainingDescription 
add constraint FK360CBF5C3BE82118 
foreign key (runIdList_id) 
references IdList;

alter table SubmitIdPickerAnalysisDescription 
add constraint FKC9B20FDF15D37E20 
foreign key (id) 
references SubmitMergedIdentificationAnalysisDescription;

alter table SubmitIdentificationAnalysisDescription 
add constraint FK441AF4DAD59FF39B 
foreign key (id) 
references SubmitJobDescription;

alter table SubmitJobDescription 
add constraint FK904FFE57D8F7CD1F 
foreign key (id) 
references ActivityDescription;

alter table SubmitMergedIdentificationAnalysisDescription 
add constraint FKD520090ED59FF39B 
foreign key (id) 
references SubmitJobDescription;

alter table SubmitMergedIdentificationAnalysisDescription 
add constraint FKD520090E31AD8452 
foreign key (identificationIds_id) 
references IdList;

alter table SubmitMergedIdentificationAnalysisDescription 
add constraint FKD520090ED133159F 
foreign key (databaseIds_id) 
references IdList;

alter table SubmitProteomicsConvertDescription 
add constraint FK6893F05ED59FF39B 
foreign key (id) 
references SubmitJobDescription;

alter table SubmitScaffoldAnalysisDescription 
add constraint FK7E34885215D37E20 
foreign key (id) 
references SubmitMergedIdentificationAnalysisDescription;

alter table SubmitThermofinniganRunJobDescription 
add constraint FKBFB08D81D59FF39B 
foreign key (id) 
references SubmitJobDescription;

alter table TropixObjectDescription 
add constraint FKD19E526FD8F7CD1F 
foreign key (id) 
references ActivityDescription;

alter table UploadFileDescription 
add constraint FKD04487FD8F7CD1F 
foreign key (id) 
references ActivityDescription;
