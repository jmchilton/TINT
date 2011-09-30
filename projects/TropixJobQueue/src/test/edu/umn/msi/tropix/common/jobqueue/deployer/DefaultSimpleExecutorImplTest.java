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

package edu.umn.msi.tropix.common.jobqueue.deployer;

import java.io.File;

import org.easymock.classextension.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.execution.process.Process;
import edu.umn.msi.tropix.common.execution.process.ProcessConfiguration;
import edu.umn.msi.tropix.common.execution.process.ProcessFactory;

public class DefaultSimpleExecutorImplTest {

  class FakeProcessFactory implements ProcessFactory {
    private ProcessConfiguration recordedProcessConfiguration;
    private int retVal;
    
    FakeProcessFactory(final int retVal) {
      this.retVal = retVal;
    }
    
    public Process createProcess(final ProcessConfiguration processConfiguration) {
      this.recordedProcessConfiguration = processConfiguration;
      final Process process = EasyMock.createMock(Process.class);
      try {
        EasyMock.expect(process.waitFor()).andStubReturn(retVal);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      EasyMock.replay(process);
      return process;
    }
    
  }
  
  
  @Test(groups = "unit")
  public void testExecuteInDirectory() {
    final FakeProcessFactory factory = new FakeProcessFactory(0);
    final SimpleExecutor simpleExecutor = new DefaultSimpleExecutorImpl(factory);
    assert 0 == simpleExecutor.executeInDirectory(new File("moo"), "foo", "x", "y");
    assert factory.recordedProcessConfiguration.getApplication().equals("foo");
    assert Iterables.elementsEqual(factory.recordedProcessConfiguration.getArguments(), Lists.newArrayList("x", "y"));
    assert factory.recordedProcessConfiguration.getDirectory().equals(new File("moo"));
  }
  
}
