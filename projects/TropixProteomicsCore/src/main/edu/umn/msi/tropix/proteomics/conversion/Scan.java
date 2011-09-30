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

import org.apache.commons.io.FilenameUtils;

import edu.umn.msi.tropix.proteomics.conversion.DtaNameUtils.DtaNameSummary;

// TODO: Construct Scan objects using the builder pattern
public class Scan implements Cloneable {
  private short precursorCharge = 0;
  private float precursorIntensity = 0.0f;
  private float precursorMz = 0.0f;
  private final int msLevel;
  private final int number;
  private int alt = 0;
  private final double[] peaks;
  private String parentFileName;
  private String parentName;
  private boolean parentFileNameExplicit;

  public boolean isPrecursorChargeSet() {
    return precursorCharge != 0;
  }

  public boolean isPrecursorIntensitySet() {
    return precursorIntensity > 0.0;
  }
  
  public boolean isPrecursorMzSet() {
    return precursorMz > 0.0;
  }
  
  public Scan(final int msLevel, final int number, final double[] peaks) {
    this.msLevel = msLevel;
    this.number = number;
    this.peaks = peaks;
  }

  public Scan clone() {
    try {
      final Scan clonedScan = (Scan) super.clone();
      return clonedScan;
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException(e);
    }
  }
  
  public double[] getPeaks() {
    return peaks;
  }

  /**
   * @return The precursorCharge of this scan, or 0 if it is unknown.
   */
  public short getPrecursorCharge() {
    return precursorCharge;
  }

  public void setPrecursorCharge(final short precursorCharge) {
    this.precursorCharge = precursorCharge;
  }

  /**
   * @return The precursorMz of this scan, or 0 if it is unknown.
   */
  public float getPrecursorMz() {
    return precursorMz;
  }

  public void setPrecursorMz(final float precursorMz) {
    this.precursorMz = precursorMz;
  }

  /**
   * @return The precursorIntensity of this scan, or 0 if it is unknown.
   */
  public float getPrecursorIntensity() {
    return precursorIntensity;
  }

  public void setPrecursorIntensity(final float precursorIntensity) {
    this.precursorIntensity = precursorIntensity;
  }

  /**
   * 
   * @return true if parent file name was explicitly defined in source file, false
   * if it was inferred. 
   */
  public boolean isParentFileNameExplicit() {
    return parentFileNameExplicit;
  }

  public void setParentFileNameExplicit(final boolean parentFileNameExplicit) {
    this.parentFileNameExplicit = parentFileNameExplicit;
  }

  
  public int getMsLevel() {
    return msLevel;
  }

  public int getNumber() {
    return number;
  }

  public int getAlt() {
    int actualAlt;
    if(alt == 0) {
      actualAlt = number;
    } else {
      actualAlt = alt;
    }
    return actualAlt;
  }

  public void setAlt(final int alt) {
    this.alt = alt;
  }

  public void setParentFileName(final String parentFilePath) {
    this.parentFileName = FilenameUtils.getName(parentFilePath);
    if(DtaNameUtils.isDtaName(parentFileName)) {
      final DtaNameSummary summary = DtaNameUtils.getDtaNameSummary(parentFileName);
      this.parentName = summary.getBasename();
      alt = summary.getStart();
      if(precursorCharge == 0) {
        precursorCharge = summary.getCharge();
      }
    } else {
      this.parentName = FilenameUtils.getBaseName(parentFileName);
    }
  }

  public String getParentFileName() {
    return parentFileName;
  }

  /**
   * If parentFileName is of the form moo.123.125.2.dta this returns moo. If the parentFileName is of the form moo.RAW this returns moo.
   * 
   */
  public String getParentName() {
    return parentName;
  }
  
  public String toString() {
    return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this);
  }
  
}