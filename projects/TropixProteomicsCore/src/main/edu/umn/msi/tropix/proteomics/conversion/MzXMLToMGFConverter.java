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
import java.util.List;

import javax.annotation.WillNotClose;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public interface MzXMLToMGFConverter {
  public static class MgfConversionOptions {
    public static enum MgfStyle {
      DEFAULT, MSM, PROTEIN_PILOT, MS2PREPROC
    };

    private MgfStyle mgfStyle = MgfStyle.DEFAULT;
    private List<Function<Scan, Scan>> scanTransformers = Lists.newArrayList();

    public MgfStyle getMgfStyle() {
      return mgfStyle;
    }

    public void setMgfStyle(final MgfStyle mgfStyle) {
      this.mgfStyle = mgfStyle;
    }

    public void addScanTransformer(final Function<Scan, Scan> scanTransformer) {
      this.scanTransformers.add(scanTransformer);
    }

    public List<Function<Scan, Scan>> getScanTransformers() {
      return scanTransformers;
    }

  }

  void mzxmlToMGF(InputStream mzxmlInputStream, @WillNotClose OutputStream mgfStream, MgfConversionOptions options);
}
