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

import edu.umn.msi.tropix.jobs.activities.descriptions.CreateScaffoldAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.IdList;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.persistence.service.ScaffoldAnalysisService;

public class CreateScaffoldAnalysisActivityFactoryImplTest extends
    BaseCreateActivityFactoryImplTest<CreateScaffoldAnalysisDescription, ScaffoldAnalysis> {

  @Test(groups = "unit")
  public void create() {
    final ScaffoldAnalysisService service = EasyMock.createMock(ScaffoldAnalysisService.class);
    getDescription().setIdentificationIds(IdList.forIterable(Lists.newArrayList(UUID.randomUUID().toString(), UUID.randomUUID().toString())));
    getDescription().setScaffoldVersion("V2");
    service.createScaffoldJob(matchId(), matchDestinationId(), captureObject(), EasyMock.aryEq(getDescription().getIdentificationIds().toArray()),
        EasyMock.eq(getDescription().getDriverFileId()), EasyMock.eq(getDescription().getOutputFileId()), EasyMock.eq("V2"));
    returnInitializedObject();
    EasyMock.replay(service);
    runAndVerify(new CreateScaffoldAnalysisActivityFactoryImpl(getFactorySupport(), service));
    EasyMock.verify(service);
  }

}
