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

package edu.umn.msi.tropix.proteomics.convert.impl;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.JobProcessor;
import edu.umn.msi.tropix.common.jobqueue.description.InProcessJobDescription;
import edu.umn.msi.tropix.common.jobqueue.test.JobProcessorFactoryTest;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.proteomics.conversion.MgfToMzxmlConverter;
import edu.umn.msi.tropix.proteomics.convert.input.ConvertParameters;
import edu.umn.msi.tropix.proteomics.convert.input.Format;

public class ProteomicsConvertJobFactoryImplTest extends JobProcessorFactoryTest<ProteomicsConvertJobProcessorFactoryImpl> {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();  
  private MgfToMzxmlConverter mgfToMzxmlConverter;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    mgfToMzxmlConverter = EasyMock.createMock(MgfToMzxmlConverter.class);
    setFactory(new ProteomicsConvertJobProcessorFactoryImpl(mgfToMzxmlConverter));
    super.init(true);
    getMockObjects().add(mgfToMzxmlConverter);    
  }
  
  @Test(groups = "unit")
  public void testMgfToMzxml() {
    final InputContext sourceContext = getDownloadContext();
    sourceContext.get(getDirMockOutputContext("moo.mgf"));
    
    final ConvertParameters convertParameters = new ConvertParameters();
    convertParameters.setInputFormat(Format.MGF);
    convertParameters.setOutputFormat(Format.MzXML);
    convertParameters.setInputName("moo.mgf");

    expectAddResource("output");
    
    getStagingDirectory().setup();
    getStagingDirectory().cleanUp();
    
    mgfToMzxmlConverter.mgfToMzXmxl(EasyMock.eq(new File(getPath(), "moo.mgf")), EasyMockUtils.copy(new ByteArrayInputStream("moo cow".getBytes())));
    
    getMockObjects().replay();    
    final JobProcessor<InProcessJobDescription> jobProcessor = getFactory().create(getJobProcessorConfiguration(), sourceContext, convertParameters);
    jobProcessor.preprocess().execute();
    jobProcessor.postprocess(true);
    
    getMockObjects().verifyAndReset();
    FILE_UTILS.readFileToString(new File(getPath(), "output")).equals("moo cow");
  }
  
}
