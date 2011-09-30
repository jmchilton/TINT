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

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateProteomicsRunDescription;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.persistence.service.ProteomicsRunService;

public class CreateProteomicsRunActivityFactoryImplTest extends BaseCreateActivityFactoryImplTest<CreateProteomicsRunDescription, ProteomicsRun> {

  @Test(groups = "unit", dataProvider = "bool2", dataProviderClass = TestNGDataProviders.class)
  public void create(final boolean setSample, final boolean setSource) {
    if(setSample) {
      getDescription().setSampleId(UUID.randomUUID().toString());
    } 
    if(setSource) {
      getDescription().setSourceId(UUID.randomUUID().toString());
    }
    final ProteomicsRunService proteomicsRunService = EasyMock.createMock(ProteomicsRunService.class);
    proteomicsRunService.createProteomicsRun(matchId(), matchDestinationId(), captureObject(), EasyMock.eq(getDescription().getMzxmlFileId()), EasyMock.eq(getDescription().getSampleId()), EasyMock.eq(getDescription().getSourceId()));
    returnInitializedObject();
    EasyMock.replay(proteomicsRunService);
    runAndVerify(new CreateProteomicsRunActivityFactoryImpl(proteomicsRunService, getFactorySupport()));
    EasyMock.verify(proteomicsRunService);    
  }

}
