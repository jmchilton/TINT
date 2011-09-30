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

import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateDatabaseDescription;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.persistence.service.DatabaseService;

public class CreateDatabaseActivityFactoryImplTest extends BaseCreateActivityFactoryImplTest<CreateDatabaseDescription, Database> {

  @Test(groups = "unit", dataProvider="bool1", dataProviderClass=TestNGDataProviders.class)
  public void createDatabase(final boolean committed) {
    final String databaseType = "fasta";
    getDescription().setDatabaseType(databaseType);
    getDescription().setCommitted(committed);
    final DatabaseService databaseService = EasyMock.createMock(DatabaseService.class);
    databaseService.createDatabase(matchId(), matchDestinationId(), captureObject(), EasyMock.eq(getDescription().getDatabaseFileId()));
    returnInitializedObject();
    EasyMock.replay(databaseService);
    runAndVerify(new CreateDatabaseActivityFactoryImpl(databaseService, getFactorySupport()));
    EasyMock.verify(databaseService);
    assert getCapturedObject().getType().equals(databaseType);
  }

}
