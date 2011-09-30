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

import java.util.List;
import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.xml.FormattedXmlUtility;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.galaxy.service.GalaxyJobQueueContext;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.galaxy.xml.GalaxyXmlUtils;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitGalaxyDescription;
import edu.umn.msi.tropix.jobs.activities.factories.GalaxySupport.GalaxyInputFile;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.persistence.service.GalaxyToolService;
import edu.umn.msi.tropix.transfer.types.HttpTransferResource;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class SubmitGalaxyActivityFactoryImplTest extends BaseSubmitActivityFactoryImplTest {
  private static final FormattedXmlUtility<Tool> TOOL_XML_UTILITY = new FormattedXmlUtility<Tool>(Tool.class);
  
  @Test(groups = "unit")
  public void submit() {    
    init();
    final String galaxyToolId = UUID.randomUUID().toString();
    final GalaxySupport galaxySupport = createMock(GalaxySupport.class);
    final GalaxyToolService galaxyToolService = createMock(GalaxyToolService.class);
    final SubmitGalaxyActivityFactoryImpl factory = new SubmitGalaxyActivityFactoryImpl(getFactorySupport(), getSubmitSupport(), galaxySupport, galaxyToolService);
    final SubmitGalaxyDescription description = TestUtils.init(new SubmitGalaxyDescription());
    final String serviceUrl = "local://Galaxy";
    description.setServiceUrl(serviceUrl);

    
    final Tool tool = new Tool();
    tool.setDescription("Descript");
    EasyMock.expect(galaxyToolService.getXml(getContext().getCredential().getIdentity(), galaxyToolId)).andReturn(TOOL_XML_UTILITY.serialize(tool));
    final RootInput input = new RootInput();
    description.setGalaxyToolId(galaxyToolId);
    description.setInput(input);

    final TransferResource resource1 = new HttpTransferResource();
    GalaxyInputFile inputFile1 = new GalaxyInputFile("filename", resource1);
    final List<GalaxyInputFile> inputFiles = Lists.newArrayList(inputFile1);
    galaxySupport.prepareInputFiles(EasyMock.isA(Tool.class), EasyMock.eq(input), EasyMock.eq(getContext().getCredential()));
    EasyMock.expectLastCall().andReturn(inputFiles);

    final GalaxyJobQueueContext context = expectCreateJob("local://Galaxy", GalaxyJobQueueContext.class);
    context.submitJob(EasyMock.eq(GalaxyXmlUtils.convert(tool)), EasyMock.eq(GalaxyXmlUtils.convert(input)), EasyMock.aryEq(new String[] {"filename"}), EasyMockUtils.arySame(new TransferResource[] {resource1}), expectCredentialResource());
    
    preRun(context);
    final Activity activity = factory.getActivity(description, getContext());
    activity.run();
    postRun(description);
  }

}
