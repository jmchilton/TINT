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

package edu.umn.msi.tropix.genomics.bowtie.impl;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.test.JobProcessorFactoryTest;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.genomics.bowtie.input.BowtieInput;

public class BowtieJobProcessorFactoryImplTest extends JobProcessorFactoryTest<BowtieJobProcessorFactoryImpl> {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private BowtieJobProcessorImpl processor;
  private BowtieInputOptionsBuilder optionsBuilder;

  @BeforeMethod(groups = "unit")
  public void init() {
    setFactory(new BowtieJobProcessorFactoryImpl());
    super.init();

    optionsBuilder = EasyMock.createMock(BowtieInputOptionsBuilder.class);
    getMockObjects().add(optionsBuilder);
  }

  @Test(groups = "unit")
  public void create() {
    final BowtieInput input = new BowtieInput();
    getStagingDirectory().setup();

    final InputContext indexContext = EasyMock.createMock(InputContext.class);
    indexContext.get(EasyMockUtils.writeContentsToFile(IO_UTILS.toByteArray(getClass().getResourceAsStream("test.ebwt.zip"))));
    getMockObjects().add(indexContext);
    final List<OutputContext> indexContexts = super.getDirMockOutputContexts("human.1.ebwt", "human.2.ebwt", "human.3.ebwt", "human.4.ebwt", "human.rev.1.ebwt", "human.rev.2.ebwt");
    for(int i = 0; i < 6; i++) {
      indexContexts.get(i).put(EasyMockUtils.inputStreamWithContents(("" + (i + 1)).getBytes()));
    }
    input.setInputsType("PAIRED");
    input.setInputsFormat("FASTA");

    final List<String> databaseNames = Lists.newArrayList("db1.fasta", "db2.fasta", "db3.fasta", "db4.fasta");
    final List<OutputContext> databaseContexts = getDirMockOutputContexts(databaseNames);
    final List<InputContext> databaseInputContexts = getDownloadContexts(4);
    for(int i = 0; i < 4; i++) {
      databaseInputContexts.get(i).get(databaseContexts.get(i));
    }

    EasyMock.expect(optionsBuilder.getCommandLineOptions(input, databaseNames, "human", null)).andReturn("OPTIONS");

    replay();

    processor = getFactory().createBowtieJob(getJobProcessorConfiguration(), input, indexContext, databaseInputContexts);
    processor.setBowtieInputOptionsBuilder(optionsBuilder);
    final ExecutableJobDescription description = processor.preprocess();
    assert Arrays.equals(description.getJobDescriptionType().getArgument(), new String[] {"OPTIONS"});
    assert description.getJobDescriptionType().getDirectory().equals(getPath());
    verifyAndReset();

    expectAddResource("output.txt");
    getStagingDirectory().cleanUp();
    replay();
    processor.postprocess(true);
    verifyAndReset();
  }

}
