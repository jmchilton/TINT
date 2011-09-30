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

package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
class ScanIndex {
  private final String name;
  private final int number;
  private final short charge;

  ScanIndex(final String name, final int number, final short charge) {
    this.name = name;
    this.number = number;
    this.charge = charge;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + charge;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + number;
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if(this == obj) {
      return true;
    }
    if(obj == null) {
      return false;
    }
    if(getClass() != obj.getClass()) {
      return false;
    }
    ScanIndex other = (ScanIndex) obj;
    if(charge != other.charge) {
      return false;
    }
    if(name == null) {
      if(other.name != null) {
        return false;
      }
    } else if(!name.equals(other.name)) {
      return false;
    }
    if(number != other.number) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ScanIndex [charge=" + charge + ", name=" + name + ", number=" + number + "]";
  }

  public boolean numberAndChargeMatch(@Nonnull final ScanIndex scanIndex) {
    return scanIndex.number == number && (scanIndex.charge == 0 || scanIndex.charge == charge);
  }


}