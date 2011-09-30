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

import java.util.UUID;

import org.easymock.classextension.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.jobs.activities.descriptions.CreateITraqQuantitationTrainingDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.IdList;
import edu.umn.msi.tropix.models.ITraqQuantitationTraining;
import edu.umn.msi.tropix.persistence.service.ITraqQuantitationTrainingService;

public class CreateITraqQuantitationTrainingActivityFactoryImplTest extends BaseCreateActivityFactoryImplTest<CreateITraqQuantitationTrainingDescription, ITraqQuantitationTraining> {

  @Test(groups = "unit")
  public void createQuantificationTraining() {
    final ITraqQuantitationTrainingService service = EasyMock.createMock(ITraqQuantitationTrainingService.class);
    getDescription().setRunIdList(IdList.forIterable(Lists.newArrayList(UUID.randomUUID().toString(), UUID.randomUUID().toString())));

    service.createQuantitationTraining(matchId(), matchDestinationId(), captureObject(), EasyMock.eq(getDescription().getReportFileId()), EasyMock.aryEq(getDescription().getRunIdList().toList().toArray(
        new String[2])), EasyMock.eq(getDescription().getOutputFileId()));
    returnInitializedObject();
    EasyMock.replay(service);
    runAndVerify(new CreateITraqQuantitationTrainingActivityFactoryImpl(service, getFactorySupport()));
    EasyMock.verify(service);
  }
}
