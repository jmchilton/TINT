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

package edu.umn.msi.tropix.common.jobqueue.jobprocessors;

import org.globus.exec.generated.JobDescriptionType;
import org.globus.gsi.GlobusCredential;
import org.springframework.core.annotation.AnnotationUtils;

import com.google.common.base.Preconditions;


import edu.umn.msi.tropix.common.io.Directories;
import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.RecoverableJobProcessorFactory;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;

public abstract class BaseExecutableJobProcessorFactoryImpl<T extends BaseExecutableJobProcessor> extends BaseJobProcessorFactoryImpl<T> implements RecoverableJobProcessorFactory<ExecutableJobDescription> {
  public static final String DEFAULT_STANDARD_OUT_FILE_NAME = "STANDARD_OUT";
  public static final String DEFAULT_STANDARD_ERROR_FILE_NAME = "STANDARD_ERROR"; 
  private boolean useStaging = true;
  private String applicationPath;
  private String workingDirectory;
  private String executionType;
  private boolean requireGlobusCredential = false;

  /**
   * This method is responsible for returning fresh new jobs ready to be preprocessed.
   */
  public final T create(final JobProcessorConfiguration config) {
    final T instance = createAndInitialize();
    final StagingDirectory stagingDirectory = getAndSetupStagingDirectory(config);
    
    if(requireGlobusCredential) {
      final Credential credential = config.getCredential();
      Preconditions.checkState(credential != null, "Valid globus credential required, but no credential found.");
      final GlobusCredential globusCredential = credential.getGlobusCredential();
      Preconditions.checkState(globusCredential != null, "Valid globus credential required, but credential has no associated globus credential.");
      Preconditions.checkState(globusCredential.getTimeLeft() > 0, "Valid globus credential required, but credential is timed-out.");
    }

    // Create jobDescription
    final JobDescriptionType jobDescription = new JobDescriptionType();
    final JobType jobType = AnnotationUtils.findAnnotation(getClass(), JobType.class);
    JobDescriptionUtils.setJobType(jobDescription, jobType.value());
    JobDescriptionUtils.setStagingDirectory(jobDescription, stagingDirectory.getAbsolutePath());
    JobDescriptionUtils.setExecutionType(jobDescription, executionType);
    JobDescriptionUtils.setProxy(jobDescription, config.getCredential());
    
    // Repeatedly having issues where programs are blocking presumably because they cannot write to standard out,
    // so I am adding defaults for standard out and standard error. Individual JobProcessor can override these.
    jobDescription.setStdout(Directories.buildAbsolutePath(stagingDirectory, DEFAULT_STANDARD_OUT_FILE_NAME));
    jobDescription.setStderr(Directories.buildAbsolutePath(stagingDirectory, DEFAULT_STANDARD_ERROR_FILE_NAME));    
    
    jobDescription.setDirectory(workingDirectory);
    jobDescription.setExecutable(applicationPath);

    instance.setJobDescription(jobDescription);
    instance.setStagingDirectory(stagingDirectory);
    return instance;
  }

  /**
   * Subclasses should implement this to create a new job of type T that can than be initialized or preprocessed depending on which method is called.
   */
  protected abstract T create();

  private T createAndInitialize() {
    final T instance = create();
    instance.setUseStaging(useStaging);
    initializeDisposableResourceTracker(instance);
    return instance;
  }

  /**
   * This method is responsible for returning instances of the JobProcessor that are recreated from previously staged jobs. Jobs returned from this methods should not be preprocessed.
   */
  public final T recover(final ExecutableJobDescription jobDescription) {
    final T instance = createAndInitialize();

    final String stagingDirectoryPath = JobDescriptionUtils.getStagingDirectory(jobDescription.getJobDescriptionType());
    final Credential proxy = JobDescriptionUtils.getProxy(jobDescription.getJobDescriptionType());
    final StagingDirectory stagingDirectory = getStagingDirectoryFactory().get(proxy, stagingDirectoryPath);
    instance.setStagingDirectory(stagingDirectory);
    instance.initialize(jobDescription);
    return instance;
  }

  public void setUseStaging(final boolean useStaging) {
    this.useStaging = useStaging;
  }

  public void setApplicationPath(final String applicationPath) {
    this.applicationPath = applicationPath;
  }

  public void setWorkingDirectory(final String workingDirectory) {
    this.workingDirectory = workingDirectory;
  }

  public void setExecutionType(final String executionType) {
    this.executionType = executionType;
  }
  
  public void setRequireGlobusCredential(final boolean requireGlobusCredential) {
    this.requireGlobusCredential = requireGlobusCredential;
  }

}
