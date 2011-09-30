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

package edu.umn.msi.tropix.galaxy.service;

import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.FileJobProcessor;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.test.BaseFileJobQueueContextImplTest;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.galaxy.inputs.cagrid.RootInput;
import edu.umn.msi.tropix.galaxy.tool.cagrid.Tool;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class GalaxyJobQueueContextImplTest extends BaseFileJobQueueContextImplTest {

  @Test(groups = "unit")
  public void testValidSubmit() {
    testSubmit(false);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testSubmitException() {
    testSubmit(true);
  }

  public void testSubmit(final boolean exception) {
    final GalaxyJobQueueContextImpl context = new GalaxyJobQueueContextImpl();
    init(context);
    final GalaxyJobProcessorFactory galaxyJobBuilder = createMock(GalaxyJobProcessorFactory.class);
    context.setGalaxyJobBuilder(galaxyJobBuilder);
    final Tool tool = new Tool();
    final RootInput input = new RootInput();
    @SuppressWarnings("unchecked")
    final FileJobProcessor<ExecutableJobDescription> fileJobProcessor = EasyMock.createMock(FileJobProcessor.class);
    final Capture<List<String>> names = EasyMockUtils.newCapture();
    final Capture<List<InputContext>> contexts = EasyMockUtils.newCapture();
    galaxyJobBuilder.createJob(expectConfiguration(), (edu.umn.msi.tropix.galaxy.tool.Tool) EasyMock.anyObject(),
        (edu.umn.msi.tropix.galaxy.inputs.RootInput) EasyMock.anyObject(), EasyMock.capture(names), EasyMock.capture(contexts));
    expectLastCallAndReturnFileJobProcessor();
    expectSubmitJob("Galaxy", exception);
    final TransferResource r1 = getReference(), r2 = getReference();
    final List<InputContext> expectedContexts = Lists.newArrayList(getDownloadContext(r1), getDownloadContext(r2));
    doReplay();
    context.submitJob(tool, input, new String[] {"file1", "file2"}, new TransferResource[] {r1, r2}, getCredentialReference());
    doVerify();
    assert Iterables.elementsEqual(names.getValue(), Lists.newArrayList("file1", "file2"));
    assert Iterables.elementsEqual(contexts.getValue(), expectedContexts);
  }

}
