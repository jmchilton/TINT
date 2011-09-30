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

package edu.umn.msi.tropix.proteomics.myrimatch.impl;

import org.globus.exec.generated.JobDescriptionType;

import edu.umn.msi.tropix.proteomics.bumbershoot.BumbershootParameterUtils;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.MyriMatchParameters;
import edu.umn.msi.tropix.proteomics.identification.IdentificationJobProcessorImpl;

public class MyriMatchJobProcessorImpl extends IdentificationJobProcessorImpl<MyriMatchParameters> {
  private static final String DATABASE_PATH = "db.fasta";
  private static final String MZXML_PATH = "input.mzXML";
  private static final String CFG_PATH = "myrimatch.cfg";

  @Override
  protected void doPreprocessing() {

    getDatabase().get(getStagingDirectory().getOutputContext(DATABASE_PATH));
    getMzxml().get(getStagingDirectory().getOutputContext(MZXML_PATH));

    storeParameters();
    final JobDescriptionType jobDescriptionType = getJobDescription().getJobDescriptionType();

    jobDescriptionType.setArgument(new String[] {"-ProteinDatabase", DATABASE_PATH, MZXML_PATH});
    jobDescriptionType.setDirectory(getStagingDirectory().getAbsolutePath());
  }

  private void storeParameters() {
    final MyriMatchParameters parameters = getParameters();
    final String propertiesString = BumbershootParameterUtils.serializeBumbershootParameters(parameters);
    super.getStagingDirectory().getOutputContext(CFG_PATH).put(propertiesString.getBytes());
  }

  @Override
  protected void doPostprocessing() {
    if(wasCompletedNormally()) {
      getResourceTracker().add(getStagingDirectory().getInputContext("input.pepXML"));
    }
  }

}
