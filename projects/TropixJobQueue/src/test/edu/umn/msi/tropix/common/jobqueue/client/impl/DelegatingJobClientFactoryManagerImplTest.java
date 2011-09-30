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

package edu.umn.msi.tropix.common.jobqueue.client.impl;

import java.util.Map;

import org.easymock.classextension.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactory;
import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactoryManager;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class DelegatingJobClientFactoryManagerImplTest {

  @Test(groups = "unit")
  public void testDelegation() {
    final DelegatingJobClientFactoryManagerImpl delegatingManager = new DelegatingJobClientFactoryManagerImpl();
    final JobClientFactoryManager manager1 = EasyMock.createMock(JobClientFactoryManager.class);
    final JobClientFactoryManager manager2 = EasyMock.createMock(JobClientFactoryManager.class);
    
    final Map<String, JobClientFactoryManager> prefixToManagerMap = Maps.newHashMap();
    prefixToManagerMap.put("prefix1", manager1);
    prefixToManagerMap.put("prefix2", manager2);
    
    delegatingManager.setJobClientFactoryManagerMap(prefixToManagerMap);
    
    final JobClientFactory mockFactory = EasyMock.createMock(JobClientFactory.class);
    EasyMock.expect(manager1.getFactory("prefix1://moo")).andReturn(mockFactory);
    EasyMockUtils.replayAll(manager1, manager2);
    
    delegatingManager.getFactory("prefix1://moo").equals(mockFactory);
    EasyMockUtils.verifyAndReset(manager1, manager2);  
  }
  
  
}
