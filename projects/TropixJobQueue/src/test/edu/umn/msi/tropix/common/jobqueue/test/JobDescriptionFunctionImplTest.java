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

package edu.umn.msi.tropix.common.jobqueue.test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import org.globus.exec.generated.JobDescriptionType;
import org.globus.exec.generated.NameValuePairType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.execution.ExecutionConfiguration;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.jobqueue.execution.system.JobDescriptionFunctionImpl;

public class JobDescriptionFunctionImplTest {
  private JobDescriptionFunctionImpl function = new JobDescriptionFunctionImpl();
  private JobDescriptionType jobDescription;
  private ExecutionConfiguration executionConfiguration;
  private FileUtils fileUtils = FileUtilsFactory.getInstance();
  private IOUtils ioUtils = IOUtilsFactory.getInstance();

  @BeforeMethod(groups = "unit")
  public void init() {
    jobDescription = new JobDescriptionType();
    executionConfiguration = null;
  }

  private void apply() {
    executionConfiguration = function.apply(jobDescription);
  }

  @Test(groups = "unit")
  public void empty() {
    final JobDescriptionFunctionImpl function = new JobDescriptionFunctionImpl();
    final JobDescriptionType jobDescription = new JobDescriptionType();
    function.apply(jobDescription);
  }

  @Test(groups = "unit")
  public void application() {
    jobDescription.setExecutable("moo");
    apply();
    assert executionConfiguration.getApplication().equals("moo");
  }

  @Test(groups = "unit")
  public void arguments() {
    final String[] args = new String[] {"a", "b", "c"};
    jobDescription.setArgument(args);
    apply();
    assert executionConfiguration.getArguments().equals(Arrays.asList(args));
  }

  @Test(groups = "unit")
  public void standardError() throws IOException {
    outRedirection(true, true);
    outRedirection(false, true);
  }

  @Test(groups = "unit")
  public void standardOut() throws IOException {
    outRedirection(true, false);
    outRedirection(false, false);
  }

  @Test(groups = "unit")
  public void standardInput() throws IOException {
    final File file = fileUtils.createTempFile("tpx", "");
    fileUtils.writeStringToFile(file, "Hello");
    jobDescription.setStdin(file.getAbsolutePath());
    apply();
    ioUtils.toString(executionConfiguration.getStandardInputStream()).equals("Hello");
  }

  @Test(groups = "unit")
  public void emptyStandardInput() throws IOException {
    final File file = fileUtils.createTempFile("tpx", "");
    assert file.delete();
    jobDescription.setStdin(file.getAbsolutePath());
    apply();
    assert executionConfiguration.getStandardInputStream() == null;
  }

  @Test(groups = "unit")
  public void directory() {
    jobDescription.setDirectory("/moo/cow");
    apply();
    executionConfiguration.getDirectory().equals(new File("/moo/cow"));
  }

  @Test(groups = "unit")
  public void environment() {
    final NameValuePairType pair1 = new NameValuePairType("moo", "cow");
    final NameValuePairType pair2 = new NameValuePairType("moo1", "cow1");
    jobDescription.setEnvironment(new NameValuePairType[] {pair1, pair2});
    apply();
    assert executionConfiguration.getEnvironment().get("moo").equals("cow");
    assert executionConfiguration.getEnvironment().get("moo1").equals("cow1");
  }

  public void outRedirection(final boolean empty, final boolean error) throws IOException {
    final File file = fileUtils.createTempFile("tpx", "");
    if(empty) {
      file.delete();
    }
    if(error) {
      jobDescription.setStderr(file.getAbsolutePath());
    } else {
      jobDescription.setStdout(file.getAbsolutePath());
    }
    apply();
    assert file.exists();
    OutputStream outputStream = null;
    if(error) {
      outputStream = executionConfiguration.getStandardErrorStream();
    } else {
      outputStream = executionConfiguration.getStandardOutputStream();
    }
    outputStream.write("Hello World".getBytes());
    outputStream.close();
    assert fileUtils.readFileToString(file).equals("Hello World");
    file.delete();
  }

}
