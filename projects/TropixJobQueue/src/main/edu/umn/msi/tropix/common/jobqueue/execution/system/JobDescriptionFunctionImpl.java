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

package edu.umn.msi.tropix.common.jobqueue.execution.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.globus.exec.generated.JobDescriptionType;
import org.globus.exec.generated.NameValuePairType;

import com.google.common.base.Function;

import edu.umn.msi.tropix.common.execution.ExecutionConfiguration;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;

public class JobDescriptionFunctionImpl implements Function<JobDescriptionType, ExecutionConfiguration> {
  private static FileUtils fileUtils = FileUtilsFactory.getInstance();

  public ExecutionConfiguration apply(final JobDescriptionType jobDescription) {
    final ExecutionConfiguration executionConfiguration = new ExecutionConfiguration();
    executionConfiguration.setApplication(jobDescription.getExecutable());
    if(jobDescription.getArgument() != null) {
      executionConfiguration.setArguments(Arrays.asList(jobDescription.getArgument()));
    }
    if(jobDescription.getDirectory() != null) {
      executionConfiguration.setDirectory(new File(jobDescription.getDirectory()));
    }
    if(jobDescription.getEnvironment() != null) {
      final Map<String, String> environment = new HashMap<String, String>();
      for(final NameValuePairType pair : jobDescription.getEnvironment()) {
        environment.put(pair.getName(), pair.getValue());
      }
      executionConfiguration.setEnvironment(environment);
    }
    executionConfiguration.setTimeout(jobDescription.getMaxWallTime());
    setupRedirection(jobDescription, executionConfiguration);
    return executionConfiguration;
  }

  private void setupRedirection(final JobDescriptionType jobDescription, final ExecutionConfiguration executionConfiguration) {
    final String standardError = jobDescription.getStderr();
    final String standardOut = jobDescription.getStdout();
    final String standardIn = jobDescription.getStdin();
    if(validPath(standardIn)) {
      final File standardInFile = new File(standardIn);
      if(standardInFile.exists()) {
        final FileInputStream inputStream = fileUtils.getFileInputStream(standardInFile);
        executionConfiguration.setStandardInputStream(inputStream);
      }
    }
    executionConfiguration.setStandardOutputStream(getOutputStream(standardOut));
    executionConfiguration.setStandardErrorStream(getOutputStream(standardError));
  }

  private FileOutputStream getOutputStream(final String path) {
    FileOutputStream fileOutputStream = null;
    if(validPath(path)) {
      final File file = new File(path);
      if(!file.exists()) {
        fileUtils.touch(file);
      }
      fileOutputStream = fileUtils.getFileOutputStream(file);
    }
    return fileOutputStream;
  }

  private boolean validPath(final String path) {
    return path != null && !path.equals("");
  }

}
