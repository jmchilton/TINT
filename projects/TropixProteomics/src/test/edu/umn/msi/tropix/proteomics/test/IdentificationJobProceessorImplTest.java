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

package edu.umn.msi.tropix.proteomics.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;

import java.io.File;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.proteomics.identification.IdentificationJobProcessorImpl;

public class IdentificationJobProceessorImplTest {

  class PublicIdentificationJobProcessor extends IdentificationJobProcessorImpl<Object> {

    @Override
    public void writeDatabase(final File file) {
      super.writeDatabase(file);
    }

    @Override
    public void writeMzXML(final File file) {
      super.writeMzXML(file);
    }

    public Object getParameters() {
      return super.getParameters();
    }
  }

  /*
  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void writeFail() throws IOException {
    final PublicIdentificationJobProcessor processor = new PublicIdentificationJobProcessor();
    final InputContext mzxmlPopulator = createMock(InputContext.class);
    processor.setInputMzXML(mzxmlPopulator);
    final File file = File.createTempFile("tmpmzxml", ".mzxml");
    FileUtils.touch(file);
    mzxmlPopulator.get(file);
    EasyMock.expectLastCall().andThrow(new NullPointerException());
    replay(mzxmlPopulator);
    processor.writeMzXML(file);
    verify(processor);
    assert !file.exists();
  }
  */

  @Test(groups = "unit")
  public void parameters() {
    final PublicIdentificationJobProcessor processor = new PublicIdentificationJobProcessor();
    final InputContext mzxmlPopulator = createMock(InputContext.class);
    final InputContext databasePopulator = createMock(InputContext.class);
    final Object parameters = new Object();

    processor.setDatabase(databasePopulator);
    processor.setInputMzXML(mzxmlPopulator);
    processor.setInputParameters(parameters);

    final File file1 = new File("file1");
    final File file2 = new File("file2");

    mzxmlPopulator.get(file1);
    databasePopulator.get(file2);

    replay(mzxmlPopulator, databasePopulator);
    assert processor.getParameters() == parameters;
    processor.writeMzXML(file1);
    processor.writeDatabase(file2);
  }
}
