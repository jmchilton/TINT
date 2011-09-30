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

package edu.umn.msi.tropix.proteomics.identification;

import java.io.File;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorImpl;

public class IdentificationJobProcessorImpl<T> extends BaseExecutableJobProcessorImpl implements IdentificationJobProcessor<T> {
  private InputContext mzxml, database;
  private T parameters;

  protected T getParameters() {
    return parameters;
  }

  protected InputContext getMzxml() {
    return mzxml;
  }

  protected InputContext getDatabase() {
    return database;
  }

  public void setInputMzXML(final InputContext mzxml) {
    this.mzxml = mzxml;
  }

  public void setInputParameters(final T parameters) {
    this.parameters = parameters;
  }

  public void setDatabase(final InputContext database) {
    this.database = database;
  }

  protected void writeMzXML(final File file) {
    mzxml.get(file);
  }

  protected void writeDatabase(final File file) {
    database.get(file);
  }

}
