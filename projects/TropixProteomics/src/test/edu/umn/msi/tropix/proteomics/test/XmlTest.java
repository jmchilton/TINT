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

import static edu.umn.msi.tropix.proteomics.xml.XMLConversionUtilities.convert;
import net.sourceforge.sashimi.mzxml.v3_0.MzXML;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.sequest.SequestParameters;
import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.proteomics.bioml.Bioml;
import edu.umn.msi.tropix.proteomics.scaffold.input.Scaffold;

public class XmlTest {

  @Test(groups = "unit")
  public void testConvert() {
    convert(convert(new Bioml()));
    convert(convert(new MzXML()));
    convert(convert(new Scaffold()));
    convert(convert(new XTandemParameters()));
    convert(convert(new SequestParameters()));
    // convert(convert(new TropixFile()));
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void xTandemConvertException() {
    convert((XTandemParameters) null);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void sequestConvertException() {
    convert((SequestParameters) null);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void caXTandemConvertException() {
    convert((edu.umn.msi.tropix.models.xtandem.cagrid.XTandemParameters) null);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void caSequestConvertException() {
    convert((edu.umn.msi.tropix.models.sequest.cagrid.SequestParameters) null);
  }

  /*
   * @Test(groups="unit",expectedExceptions=RuntimeException.class) public void tropixFileException() { convert((edu.umn.msi.tropix.models.cagrid.TropixFile) null); }
   */

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void nullJaxb() {
    convert((Scaffold) null);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void nullCagrid() {
    convert((edu.umn.msi.tropix.proteomics.scaffold.input.cagrid.Scaffold) null);
  }

}
