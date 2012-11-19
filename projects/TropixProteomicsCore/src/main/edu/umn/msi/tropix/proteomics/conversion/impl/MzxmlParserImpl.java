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

package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.InputStream;

import javax.annotation.WillNotClose;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import edu.umn.msi.tropix.proteomics.conversion.MzxmlParser;

public class MzxmlParserImpl implements MzxmlParser {

  static DatatypeFactory xmlDatatypeFactory = null;
  static {
    try {
      xmlDatatypeFactory = DatatypeFactory.newInstance();
    } catch(DatatypeConfigurationException e) {
      e.printStackTrace();
    }
  }

  public MzxmlInfo parse(@WillNotClose final InputStream mzxmlStream) {
    final MzxmlScanIterator scanIterator = new MzxmlScanIterator(mzxmlStream);
    return new MzxmlInfo(scanIterator);
  }

}
