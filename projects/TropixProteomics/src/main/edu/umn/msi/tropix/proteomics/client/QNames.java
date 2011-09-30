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

package edu.umn.msi.tropix.proteomics.client;

import javax.xml.namespace.QName;

/**
 * Hope to migrate all proteomics related QNames into this file,
 * this will ease potential refactorings in the future and use from
 * other packages.
 * 
 * @author John Chilton
 *
 */
public class QNames {
  /**
   * QName for XML that describes the weights produced by QuantificationTraining.
   */
  public static final QName QUANTIFICATION_WEIGHTS_QNAME = new QName("http://msi.umn.edu/tropix/proteomics/itraqquantitation/weight", "quantificationWeights");
}
