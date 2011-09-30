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

package edu.umn.msi.tropix.proteomics.sequest.impl;

import java.util.regex.Pattern;

import edu.umn.msi.tropix.proteomics.sequest.SequestOutputType;

public enum SequestOutputTypeEnum implements SequestOutputType {
  PVM_LOG("\\s*Searched dta file .* on .*\\s*"), PVM_OUTPUT("\\s*Received a request, sending dta file .* to .*...\\s*"), STANDARD_OUTPUT("\\s*Reading input file .* ...\\s*");

  private Pattern dtaLinePattern;

  private SequestOutputTypeEnum(final String dtaLinePattern) {
    this.dtaLinePattern = Pattern.compile(dtaLinePattern);
  }

  public Pattern getDtaLinePattern() {
    return dtaLinePattern;
  }
}
