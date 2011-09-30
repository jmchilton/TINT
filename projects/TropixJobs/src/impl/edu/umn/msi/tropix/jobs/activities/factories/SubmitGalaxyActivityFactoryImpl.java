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

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.xml.FormattedXmlUtility;
import edu.umn.msi.tropix.galaxy.service.GalaxyJobQueueContext;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.galaxy.xml.GalaxyXmlUtils;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitGalaxyDescription;
import edu.umn.msi.tropix.jobs.activities.factories.GalaxySupport.GalaxyInputFile;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.persistence.service.GalaxyToolService;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@ManagedBean @ActivityFactoryFor(SubmitGalaxyDescription.class)
class SubmitGalaxyActivityFactoryImpl implements ActivityFactory<SubmitGalaxyDescription> {
  private static final FormattedXmlUtility<Tool> TOOL_XML_UTILITY = new FormattedXmlUtility<Tool>(Tool.class);  
  private final FactorySupport factorySupport;
  private final GalaxyToolService galaxyToolService;
  private final SubmitJobFactorySupport submitFactorySupport;
  private final GalaxySupport galaxySupport;
  
  @Inject
  SubmitGalaxyActivityFactoryImpl(final FactorySupport factorySupport, final SubmitJobFactorySupport submitFactorySupport, final GalaxySupport galaxySupport, final GalaxyToolService galaxyToolService) {
    this.factorySupport = factorySupport;
    this.submitFactorySupport = submitFactorySupport;
    this.galaxySupport = galaxySupport;
    this.galaxyToolService = galaxyToolService;
  }

  class SubmitGalaxyActivityImpl extends BaseSubmitJobActivityImpl<SubmitGalaxyDescription> {

    protected SubmitGalaxyActivityImpl(final SubmitGalaxyDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext, factorySupport, submitFactorySupport);
    }

    public void run() {
      final Tool tool = TOOL_XML_UTILITY.deserialize(galaxyToolService.getXml(getUserId(), getDescription().getGalaxyToolId()).getBytes());
      final List<GalaxyInputFile> inputFiles = galaxySupport.prepareInputFiles(tool, getDescription().getInput(), getCredential());
      final List<TransferResource> references = Lists.newArrayList();
      final List<String> fileNames = Lists.newArrayList();
      for(final GalaxyInputFile inputFile : inputFiles) {
        references.add(inputFile.getTransferResource());
        fileNames.add(inputFile.getFileName());
      }
      
      final GalaxyJobQueueContext context = createContext(GalaxyJobQueueContext.class);
      context.submitJob(GalaxyXmlUtils.convert(tool), GalaxyXmlUtils.convert(getDescription().getInput()), Iterables.toArray(fileNames, String.class), Iterables.toArray(references, TransferResource.class), getDelegatedCredential());
      getDescription().setTicket(context.getTicket().getValue());
    }

  }
  
  public Activity getActivity(final SubmitGalaxyDescription activityDescription, final ActivityContext activityContext) {
    return new SubmitGalaxyActivityImpl(activityDescription, activityContext);
  }

}
