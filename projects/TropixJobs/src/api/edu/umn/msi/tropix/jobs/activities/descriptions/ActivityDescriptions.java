/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.jobs.activities.descriptions;

/**
 * Contains convenience methods for building up certain commonly used
 * activity descriptions.
 * 
 * @author John Chilton
 * 
 */
public class ActivityDescriptions {

  public static UploadFileDescription createUploadFileDescription(final JobDescription jobDescription, final String inputFilePath) {
    final UploadFileDescription uploadFileDescription = new UploadFileDescription();
    uploadFileDescription.setJobDescription(jobDescription);
    uploadFileDescription.setInputFilePath(inputFilePath);
    return uploadFileDescription;
  }

  public static void initCommonMetadata(final TropixObjectDescription objectDescription, final CommonMetadataProvider commonMetadataProvider) {
    objectDescription.setName(commonMetadataProvider.getName());
    objectDescription.setDescription(commonMetadataProvider.getDescription());
    objectDescription.setDestinationId(commonMetadataProvider.getDestinationId());
  }

  public static CommitObjectDescription createCommitDescription(final TropixObjectDescription tropixObjectDescription) {
    final CommitObjectDescription commitDescription = new CommitObjectDescription();
    commitDescription.setJobDescription(tropixObjectDescription.getJobDescription());
    commitDescription.setDestinationId(tropixObjectDescription.getDestinationId());
    commitDescription.addDependency(ActivityDependency.Builder.on(tropixObjectDescription).produces("objectId").consumes("objectId").build());
    commitDescription.addDependency(ActivityDependency.Builder.on(tropixObjectDescription).produces("destinationId").consumes("destinationId")
        .build());
    return commitDescription;
  }

  public static SubmitProteomicsConvertDescription createSubmitProteomicsConvert(final CreateTropixFileDescription createSourceFileDescription,
      final String serviceUrl, final String name) {
    final SubmitProteomicsConvertDescription submitDescription = new SubmitProteomicsConvertDescription();
    submitDescription.setJobDescription(createSourceFileDescription.getJobDescription());
    submitDescription.setServiceUrl(serviceUrl);
    submitDescription.setInputName(name);
    submitDescription.setInputFormat("MGF");
    submitDescription.setOutputFormat("MzXML");
    submitDescription.addDependency(ActivityDependency.Builder.on(createSourceFileDescription).produces("objectId").consumes("inputFileId").build());
    return submitDescription;
  }

  public static SubmitThermofinniganRunJobDescription createSubmitThermo(final CreateTropixFileDescription createRawFileDescription,
      final String serviceUrl, final String rawFileBaseName) {
    final SubmitThermofinniganRunJobDescription submitDescription = new SubmitThermofinniganRunJobDescription();
    submitDescription.setJobDescription(createRawFileDescription.getJobDescription());
    submitDescription.setServiceUrl(serviceUrl);
    submitDescription.setRawFileBaseName(rawFileBaseName);
    submitDescription.addDependency(ActivityDependency.Builder.on(createRawFileDescription).produces("objectId").consumes("rawFileId").build());
    return submitDescription;
  }
  
  public static SubmitThermofinniganRunJobDescription createSubmitThermo(final CreateProteomicsRunDescription run,
      final String serviceUrl, final String rawFileBaseName) {
    final SubmitThermofinniganRunJobDescription submitDescription = new SubmitThermofinniganRunJobDescription();
    submitDescription.setJobDescription(run.getJobDescription());
    submitDescription.setServiceUrl(serviceUrl);
    submitDescription.setRawFileBaseName(rawFileBaseName);
    submitDescription.setRawFileId(run.getSourceId());
    return submitDescription;
  }
  
  

  public static SubmitScaffoldAnalysisDescription createSubmitScaffold(final CreateScaffoldDriverDescription createDriver,
      final CreateTropixFileDescription createDriverFile, final String serviceUrl) {
    final SubmitScaffoldAnalysisDescription submitDescription = new SubmitScaffoldAnalysisDescription();
    populateSubmitMergedIdentificationAnalysisDescription(createDriver, createDriverFile, serviceUrl, submitDescription);
    /*
     * submitDescription.setJobDescription(createDriver.getJobDescription());
     * submitDescription.setServiceUrl(serviceUrl);
     * submitDescription.addDependency(ActivityDependency.Builder.on(createDriverFile).produces("objectId").consumes("driverFileId").build());
     * submitDescription.addDependency(ActivityDependency.Builder.on(createDriver).produces("databaseIds").consumes("databaseIds").build());
     * submitDescription.addDependency(ActivityDependency.Builder.on(createDriver).produces("identificationAnalysisIds").consumes("identificationIds").
     * build());
     */
    return submitDescription;
  }

  public static CreateScaffoldAnalysisDescription createScaffoldAnalysis(final CreateMergedIdentificationParametersDescription createDriver,
      final CreateTropixFileDescription createDriverFile, final CreateTropixFileDescription createAnalysisFile) {
    final CreateScaffoldAnalysisDescription createAnalysis = new CreateScaffoldAnalysisDescription();
    createAnalysis.setJobDescription(createDriver.getJobDescription());
    createAnalysis.addDependency(ActivityDependency.Builder.on(createDriverFile).produces("objectId").consumes("driverFileId").build());
    createAnalysis.addDependency(ActivityDependency.Builder.on(createDriver).produces("identificationAnalysisIds").consumes("identificationIds")
        .build());
    createAnalysis.addDependency(ActivityDependency.Builder.on(createAnalysisFile).produces("objectId").consumes("outputFileId").build());
    return createAnalysis;
  }

  public static SubmitIdentificationAnalysisDescription createSubmitIdentification(final CreateIdentificationParametersDescription createParameters,
      final String databaseId, final String runId, final String serviceAddress) {
    final SubmitIdentificationAnalysisDescription submitDescription = new SubmitIdentificationAnalysisDescription();
    submitDescription.setParameterType(createParameters.getParameterType());
    submitDescription.setServiceUrl(serviceAddress);
    submitDescription.setDatabaseId(databaseId);
    submitDescription.setRunId(runId);
    submitDescription.addDependency(ActivityDependency.Builder.on(createParameters).produces("parametersId").consumes("parametersId").build());
    return submitDescription;
  }

  public static CreateTropixFileDescription createFileForScaffoldDriver(final CreateMergedIdentificationParametersDescription driver) {
    final CreateTropixFileDescription createDriverFile = new CreateTropixFileDescription();
    createDriverFile.setJobDescription(driver.getJobDescription());
    createDriverFile.setCommitted(true);
    createDriverFile.addDependency(ActivityDependency.Builder.on(driver).produces("driverFileId").consumes("fileId").build());
    return createDriverFile;
  }

  public static CreateTropixFileDescription createFileFromUpload(final UploadFileDescription uploadFileDescription, final boolean committed) {
    final CreateTropixFileDescription createFileFromUploadDescription = new CreateTropixFileDescription();
    createFileFromUploadDescription.setJobDescription(uploadFileDescription.getJobDescription());
    createFileFromUploadDescription.addDependency(ActivityDependency.Builder.on(uploadFileDescription).produces("fileId").consumes("fileId").build());
    createFileFromUploadDescription.setCommitted(committed);
    return createFileFromUploadDescription;
  }

  public static PollJobDescription buildPollDescription(final SubmitJobDescription submitDescription) {
    final PollJobDescription pollDescription = new PollJobDescription();
    pollDescription.setJobDescription(submitDescription.getJobDescription());
    pollDescription.setServiceUrl(submitDescription.getServiceUrl());
    pollDescription.addDependency(ActivityDependency.Builder.on(submitDescription).consumes("ticket").produces("ticket").build());
    return pollDescription;
  }

  public static CreateTropixFileDescription buildCreateResultFile(final PollJobDescription resultDescription) {
    return buildCreateResultFile(resultDescription, 0);
  }

  public static CreateTropixFileDescription buildCreateResultFile(final PollJobDescription resultDescription, final int index) {
    final CreateTropixFileDescription createResultFileDescription = new CreateTropixFileDescription();
    createResultFileDescription.setJobDescription(resultDescription.getJobDescription());
    createResultFileDescription.addDependency(ActivityDependency.Builder.on(resultDescription).produces("fileIds").withIndex(index)
        .consumes("fileId").build());
    return createResultFileDescription;
  }

  public static SubmitIdPickerAnalysisDescription createSubmitIdPicker(final CreateIdPickerParametersDescription createDriver,
      final CreateTropixFileDescription createDriverFile, final String serviceUrl) {
    final SubmitIdPickerAnalysisDescription submitDescription = new SubmitIdPickerAnalysisDescription();
    populateSubmitMergedIdentificationAnalysisDescription(createDriver, createDriverFile, serviceUrl, submitDescription);
    return submitDescription;
  }

  private static void populateSubmitMergedIdentificationAnalysisDescription(final CreateMergedIdentificationParametersDescription createDriver,
      final CreateTropixFileDescription createDriverFile, final String serviceUrl,
      final SubmitMergedIdentificationAnalysisDescription submitDescription) {
    submitDescription.setJobDescription(createDriver.getJobDescription());
    submitDescription.setServiceUrl(serviceUrl);
    submitDescription.addDependency(ActivityDependency.Builder.on(createDriverFile).produces("objectId").consumes("driverFileId").build());
    submitDescription.addDependency(ActivityDependency.Builder.on(createDriver).produces("databaseIds").consumes("databaseIds").build());
    submitDescription.addDependency(ActivityDependency.Builder.on(createDriver).produces("identificationAnalysisIds").consumes("identificationIds")
        .build());
  }

}
