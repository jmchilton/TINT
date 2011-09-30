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

package edu.umn.msi.tropix.proteomics.service.impl;

import static edu.umn.msi.tropix.proteomics.xml.XMLConversionUtilities.convert;

import com.google.common.base.Function;

import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.models.xtandem.cagrid.XTandemParameters;
import edu.umn.msi.tropix.proteomics.service.XTandemJobQueueContext;

@JobType("XTandem")
public class XTandemJobQueueContextImpl extends IdentificationJobQueueContextImpl<XTandemParameters, edu.umn.msi.tropix.models.xtandem.XTandemParameters> implements XTandemJobQueueContext {
  private static final Function<XTandemParameters, edu.umn.msi.tropix.models.xtandem.XTandemParameters> CONVERSION_FUNCTION = new Function<XTandemParameters, edu.umn.msi.tropix.models.xtandem.XTandemParameters>() {
    public edu.umn.msi.tropix.models.xtandem.XTandemParameters apply(final XTandemParameters params) {
      return convert(params);
    }
  };

  public XTandemJobQueueContextImpl() {
    super(CONVERSION_FUNCTION);
  }
}
