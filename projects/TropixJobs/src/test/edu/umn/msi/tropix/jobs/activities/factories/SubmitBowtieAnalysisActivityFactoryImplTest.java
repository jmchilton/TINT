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

package edu.umn.msi.tropix.jobs.activities.factories;

import java.util.Map;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.genomics.bowtie.input.BowtieInput;
import edu.umn.msi.tropix.genomics.bowtie.input.InputFormat;
import edu.umn.msi.tropix.genomics.bowtie.input.InputType;
import edu.umn.msi.tropix.genomics.service.BowtieJobQueueContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.IdList;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitBowtieAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.models.BowtieIndex;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;

public class SubmitBowtieAnalysisActivityFactoryImplTest extends BaseSubmitActivityFactoryImplTest {

  @Test(groups = "unit")
  public void submit() {
    init();
    final SubmitBowtieAnalysisActivityFactoryImpl factory = new SubmitBowtieAnalysisActivityFactoryImpl(getFactorySupport(), getSubmitSupport());
    final SubmitBowtieAnalysisDescription description = TestUtils.init(new SubmitBowtieAnalysisDescription());
    final String serviceUrl = "http://Bowtie";
    description.setServiceUrl(serviceUrl);

    final Database d1 = new Database();
    final TropixFile f1 = TestUtils.getNewTropixFile();
    d1.setDatabaseFile(f1);

    final Database d2 = new Database();
    final TropixFile f2 = TestUtils.getNewTropixFile();
    d2.setDatabaseFile(f2);

    final BowtieIndex bowtieIndex = new BowtieIndex();
    final TropixFile f3 = TestUtils.getNewTropixFile();
    bowtieIndex.setIndexesFile(f3);

    getFactorySupport().saveObjects(d1, d2, f1, f2, f3, bowtieIndex);
    description.setDatabaseIds(IdList.forIterable(Lists.newArrayList(d1.getId(), d2.getId())));
    description.setIndexId(bowtieIndex.getId());

    final BowtieInput bowtieInput = new BowtieInput();
    final Map<String, String> parameters = Maps.newHashMap();
    ParameterUtils.setMapFromParameters(bowtieInput, parameters);
    parameters.remove("inputFormat");
    parameters.remove("inputType");
    bowtieInput.setInputsFormat("FASTA");
    bowtieInput.setInputsType("PAIRED");
    description.setParameterSet(StringParameterSet.fromMap(parameters));
    description.setInputFormat(InputFormat.FASTA.toString());
    description.setInputType(InputType.PAIRED.toString());

    final BowtieJobQueueContext context = expectCreateJob(serviceUrl, BowtieJobQueueContext.class);
    context.submitJob(expectDownload(f3), expectDownloads(f1, f2), expectCredentialResource(), EasyMock.eq(bowtieInput));

    preRun(context);
    final Activity activity = factory.getActivity(description, getContext());
    activity.run();
    postRun(description);
  }

}
