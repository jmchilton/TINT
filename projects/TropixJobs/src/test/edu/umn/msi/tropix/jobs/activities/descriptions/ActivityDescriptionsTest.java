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

import java.util.UUID;

import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.jobs.activities.WorkflowVerificationUtils;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import edu.umn.msi.tropix.proteomics.convert.input.Format;

public class ActivityDescriptionsTest {
  @Test(groups = "unit", timeOut = 1000)
  public void constructor() {
    new ActivityDescriptions();
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testCreateFromResult() {
    final PollJobDescription pollDescription = new PollJobDescription();
    pollDescription.setJobDescription(new JobDescription());
    final CreateTropixFileDescription resultDescription = ActivityDescriptions.buildCreateResultFile(pollDescription, 2);
    WorkflowVerificationUtils.assertJobDescriptionCopied(pollDescription, resultDescription);
    WorkflowVerificationUtils.checkDependencies(resultDescription);
    assert Iterables.getOnlyElement(resultDescription.getDependencies()).getIndex() == 2;
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testCreateFromSoleResult() {
    final PollJobDescription pollDescription = new PollJobDescription();
    pollDescription.setJobDescription(new JobDescription());
    final CreateTropixFileDescription resultDescription = ActivityDescriptions.buildCreateResultFile(pollDescription);
    WorkflowVerificationUtils.assertJobDescriptionCopied(pollDescription, resultDescription);
    WorkflowVerificationUtils.checkDependencies(resultDescription);
    assert Iterables.getOnlyElement(resultDescription.getDependencies()).getIndex() == 0;
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testSubmitProteomicsConvert() {
    final CreateTropixFileDescription sourceDescription = new CreateTropixFileDescription();
    sourceDescription.setJobDescription(new JobDescription());
    final SubmitProteomicsConvertDescription submitDescription = ActivityDescriptions.createSubmitProteomicsConvert(new FileSourceHolder(
        sourceDescription), "http://moo",
        "foo");
    WorkflowVerificationUtils.assertJobDescriptionCopied(sourceDescription, submitDescription);
    assert submitDescription.getInputName().equals("foo");
    assert submitDescription.getServiceUrl().equals("http://moo");
    assert submitDescription.getInputFormat().equals(Format.MGF.toString());
    assert submitDescription.getOutputFormat().equals(Format.MzXML.toString());
    WorkflowVerificationUtils.checkDependencies(submitDescription);
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testSubmitIdPicker() {
    final CreateTropixFileDescription driverFileDescription = new CreateTropixFileDescription();
    final CreateIdPickerParametersDescription createDriver = new CreateIdPickerParametersDescription();
    createDriver.setJobDescription(new JobDescription());

    final SubmitIdPickerAnalysisDescription submitDescription = ActivityDescriptions.createSubmitIdPicker(createDriver, driverFileDescription,
        "http://IdPicker");
    WorkflowVerificationUtils.assertJobDescriptionCopied(createDriver, submitDescription);
    WorkflowVerificationUtils.checkDependencies(submitDescription);
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testSubmitScaffold() {
    final CreateTropixFileDescription driverFileDescription = new CreateTropixFileDescription();
    final CreateScaffoldDriverDescription createDriver = new CreateScaffoldDriverDescription();
    createDriver.setJobDescription(new JobDescription());

    final SubmitScaffoldAnalysisDescription submitDescription = ActivityDescriptions.createSubmitScaffold(createDriver, driverFileDescription,
        "http://Scaffold");
    WorkflowVerificationUtils.assertJobDescriptionCopied(createDriver, submitDescription);
    WorkflowVerificationUtils.checkDependencies(submitDescription);
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testSubmitIdentification() {
    final CreateIdentificationParametersDescription parameters = new CreateIdentificationParametersDescription();
    parameters.setParameterType(IdentificationType.OMSSA.getParameterType());
    parameters.setJobDescription(new JobDescription());

    final String databaseId = UUID.randomUUID().toString();
    final String runId = UUID.randomUUID().toString();

    final SubmitIdentificationAnalysisDescription submitDescription = ActivityDescriptions.createSubmitIdentification(parameters, databaseId, runId,
        "local://Omssa");
    WorkflowVerificationUtils.checkDependencies(submitDescription);
    assert submitDescription.getParameterType().equals(IdentificationType.OMSSA.getParameterType());
    assert submitDescription.getServiceUrl().equals("local://Omssa");
  }

}
