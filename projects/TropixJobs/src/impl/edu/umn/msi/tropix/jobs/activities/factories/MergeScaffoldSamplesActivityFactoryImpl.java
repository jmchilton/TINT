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

import java.util.Iterator;
import java.util.List;

import javax.annotation.ManagedBean;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.Id;
import edu.umn.msi.tropix.jobs.activities.descriptions.IdList;
import edu.umn.msi.tropix.jobs.activities.descriptions.MergeScaffoldSamplesDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ScaffoldSample;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;

@ManagedBean
@ActivityFactoryFor(MergeScaffoldSamplesDescription.class)
class MergeScaffoldSamplesActivityFactoryImpl implements ActivityFactory<MergeScaffoldSamplesDescription> {

  private class MergeScaffoldSamplesActivityImpl implements Activity {
    private final MergeScaffoldSamplesDescription activityDescription;

    public MergeScaffoldSamplesActivityImpl(final MergeScaffoldSamplesDescription activityDescription) {
      this.activityDescription = activityDescription;
    }

    public void run() {
      if(activityDescription.getProduceMultipleSamples()) {
        final int size = activityDescription.getNames().getIds().size();
        final int numIds = activityDescription.getIdentificationIds().getIds().size();
        Preconditions.checkState(size == numIds, String.format("MergeScaffoldSamplesDescription has %d names and %d ids", size, numIds));
        final List<ScaffoldSample> samples = Lists.newArrayListWithExpectedSize(size);
        final Iterator<Id> namesIter = activityDescription.getNames().getIds().iterator();
        final Iterator<Id> identificationIdIter = activityDescription.getIdentificationIds().getIds().iterator();
        while(namesIter.hasNext()) {
          final String name = namesIter.next().getValue();
          final String identificationId = identificationIdIter.next().getValue();
          final ScaffoldSample sample = new ScaffoldSample();
          sample.setAnalyzeAsMudpit(activityDescription.getMudpit());
          sample.setIdentificationAnalysisIds(IdList.forIterable(Lists.newArrayList(identificationId)));
          sample.setSampleName(name);
          samples.add(sample);
        }
        activityDescription.setScaffoldSamples(samples);
      } else {
        Preconditions.checkState(activityDescription.getNames().getIds().size() == 1);
        final ScaffoldSample sample = new ScaffoldSample();
        sample.setAnalyzeAsMudpit(activityDescription.getMudpit());
        sample.setIdentificationAnalysisIds(new IdList(activityDescription.getIdentificationIds()));
        sample.setSampleName(activityDescription.getNames().getIds().get(0).getValue());
        activityDescription.setScaffoldSamples(Lists.newArrayList(sample));
      }
    }
  }

  public Activity getActivity(final MergeScaffoldSamplesDescription activityDescription, final ActivityContext activityContext) {
    return new MergeScaffoldSamplesActivityImpl(activityDescription);
  }

}
