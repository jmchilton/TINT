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

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdentificationAnalysisDescription;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.persistence.service.IdentificationAnalysisService;
import edu.umn.msi.tropix.proteomics.cagrid.metadata.ParameterType;

public class CreateIdentificationAnalysisActivityFactoryImplTest extends BaseCreateActivityFactoryImplTest<CreateIdentificationAnalysisDescription, IdentificationAnalysis> {

  @Test(groups = "unit")
  public void createSequest() {
    final IdentificationAnalysisService identificationAnalysisService = EasyMock.createMock(IdentificationAnalysisService.class);
    getDescription().setAnalysisType(ParameterType.SequestBean.getValue());
    identificationAnalysisService.createIdentificationAnalysis(matchId(), matchDestinationId(), captureObject(), EasyMock.eq(getDescription().getAnalysisType()), EasyMock.eq(getDescription()
        .getAnalysisFileId()), EasyMock.eq(getDescription().getRunId()), EasyMock.eq(getDescription().getDatabaseId()), EasyMock.eq(getDescription().getParametersId()));
    returnInitializedObject();
    EasyMock.replay(identificationAnalysisService);
    runAndVerify(new CreateIdentificationAnalysisActivityFactoryImpl(identificationAnalysisService, getFactorySupport()));
    EasyMock.verify(identificationAnalysisService);
    final IdentificationAnalysis createdAnalysis = getCapturedObject();
    assert createdAnalysis.getIdentificationProgram().equals(ParameterType.SequestBean.getValue());
  }

}
