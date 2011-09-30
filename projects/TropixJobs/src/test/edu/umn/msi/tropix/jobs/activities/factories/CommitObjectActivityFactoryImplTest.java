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

import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommitObjectDescription;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;

public class CommitObjectActivityFactoryImplTest {

  @Test(groups = "unit")
  public void commit() {
    final MockFactorySupportImpl mockFactorySupport = new MockFactorySupportImpl();
    final TropixObjectService tropixObjectService = EasyMock.createMock(TropixObjectService.class);
    final ActivityContext context = TestUtils.getContext();
    final CommitObjectDescription description = TestUtils.init(new CommitObjectDescription());
    tropixObjectService.commit(context.getCredential().getIdentity(), description.getObjectId());
    EasyMock.replay(tropixObjectService);
    CommitObjectActivityFactoryImpl factory = new CommitObjectActivityFactoryImpl(tropixObjectService, mockFactorySupport);
    factory.getActivity(description, context).run();
    EasyMock.verify(tropixObjectService);
  }
  
}
