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

import java.util.List;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.jobs.activities.descriptions.MergeScaffoldSamplesDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ScaffoldSample;

public class MergeScaffoldSamplesActivityFactoryImplTest {

  @Test(groups ="unit", dataProvider = "bool1", dataProviderClass=TestNGDataProviders.class)
  public void testMergeToOne(final boolean asMudipt) {
    final MergeScaffoldSamplesActivityFactoryImpl factory = new MergeScaffoldSamplesActivityFactoryImpl();
    final MergeScaffoldSamplesDescription description = new MergeScaffoldSamplesDescription();
    description.setMudpit(asMudipt);
    description.setProduceMultipleSamples(false);
    description.addName("n1");
    description.addIdentificationId("id1");
    description.addIdentificationId("id2");
    
    factory.getActivity(description, TestUtils.getContext()).run();
    
    final List<ScaffoldSample> samples = description.getScaffoldSamples();
    assert samples.size() == 1 : samples.size();
    assert samples.get(0).getSampleName().equals("n1");
    assert samples.get(0).getIdentificationAnalysisIds().getIds().size() == 2;
    final String firstId = samples.get(0).getIdentificationAnalysisIds().getIds().get(0).getValue(); 
    assert firstId.equals("id1") : firstId;
    final String secondId = samples.get(0).getIdentificationAnalysisIds().getIds().get(1).getValue(); 
    assert secondId.equals("id2") : secondId;
    assert samples.get(0).getAnalyzeAsMudpit() == asMudipt;    
  }

  
  @Test(groups ="unit", dataProvider = "bool1", dataProviderClass=TestNGDataProviders.class)
  public void testMergeToMultiple(final boolean asMudipt) {
    final MergeScaffoldSamplesActivityFactoryImpl factory = new MergeScaffoldSamplesActivityFactoryImpl();    
    final MergeScaffoldSamplesDescription description = new MergeScaffoldSamplesDescription();
    description.setMudpit(asMudipt);
    description.setProduceMultipleSamples(true);
    description.addName("n1");
    description.addIdentificationId("id1");
    description.addName("n2");
    description.addIdentificationId("id2");
    
    factory.getActivity(description, TestUtils.getContext()).run();
    
    final List<ScaffoldSample> samples = description.getScaffoldSamples();
    assert samples.size() == 2 : samples.size();
    assert samples.get(0).getSampleName().equals("n1");
    assert samples.get(0).getIdentificationAnalysisIds().getIds().size() == 1;
    final String firstId = samples.get(0).getIdentificationAnalysisIds().getIds().get(0).getValue(); 
    assert firstId.equals("id1") : firstId;
    assert samples.get(0).getAnalyzeAsMudpit() == asMudipt;

    assert samples.get(1).getSampleName().equals("n2");
    assert samples.get(1).getIdentificationAnalysisIds().getIds().size() == 1;
    final String secondId = samples.get(1).getIdentificationAnalysisIds().getIds().get(0).getValue(); 
    assert secondId.equals("id2") : secondId;
    assert samples.get(1).getAnalyzeAsMudpit() == asMudipt;    
    
  }
  
}
