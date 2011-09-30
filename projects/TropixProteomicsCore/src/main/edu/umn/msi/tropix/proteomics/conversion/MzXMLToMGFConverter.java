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

package edu.umn.msi.tropix.proteomics.conversion;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.WillNotClose;

public interface MzXMLToMGFConverter {
  public static class MgfConversionOptions {
    public static enum MgfStyle {
      DEFAULT, MSM, PROTEIN_PILOT
    };

    private MgfStyle mgfStyle = MgfStyle.DEFAULT;

    public MgfStyle getMgfStyle() {
      return mgfStyle;
    }

    public void setMgfStyle(final MgfStyle mgfStyle) {
      this.mgfStyle = mgfStyle;
    }
  }

  void mzxmlToMGF(InputStream mzxmlInputStream, @WillNotClose OutputStream mgfStream, MgfConversionOptions options);
}
