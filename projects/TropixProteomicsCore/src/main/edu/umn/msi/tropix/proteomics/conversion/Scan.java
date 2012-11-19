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

import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import edu.umn.msi.tropix.proteomics.conversion.DtaNameUtils.DtaNameSummary;
import edu.umn.msi.tropix.proteomics.conversion.Scan.Peak;

// TODO: Construct Scan objects using the builder pattern
public class Scan implements Cloneable, Iterable<Peak> {
  public static double DEFAULT_PRECURSOR_MZ = 0.0d;
  public static double DEFAULT_PRECURSOR_INTENSITY = 0.0d;
  public static short DEFAULT_PRECURSOR_CHARGE = 0;

  private short precursorCharge = DEFAULT_PRECURSOR_CHARGE;
  private double precursorIntensity = DEFAULT_PRECURSOR_INTENSITY;
  private double precursorMz = DEFAULT_PRECURSOR_MZ;
  private long rt = -1L;
  private final int index;
  private final int msLevel;
  private final int number;
  private int alt = 0;
  private double[] peaks;
  private String parentFileName;
  private String parentName;
  private boolean parentFileNameExplicit;

  public Scan(final Scan other) {
    this.precursorCharge = other.precursorCharge;
    this.precursorIntensity = other.precursorIntensity;
    this.precursorMz = other.precursorMz;
    this.rt = other.rt;
    this.index = other.index;
    this.msLevel = other.msLevel;
    this.number = other.number;
    this.alt = other.alt;
    this.peaks = other.peaks;
    this.parentFileName = other.parentFileName;
    this.parentName = other.parentName;
    this.parentFileNameExplicit = other.parentFileNameExplicit;
  }

  public boolean isPrecursorChargeSet() {
    return precursorCharge != 0;
  }

  public boolean isPrecursorIntensitySet() {
    return precursorIntensity > 0.0f;
  }

  public boolean isPrecursorMzSet() {
    return precursorMz > 0.0f;
  }

  public boolean isRtSet() {
    return rt != -1L;
  }

  public long getRt() {
    return rt;
  }

  public Scan(final int msLevel, final int index, final int number, final double[] peaks) {
    this.msLevel = msLevel;
    this.index = index;
    this.number = number;
    this.peaks = peaks;
  }

  private static double[] mergeMzAndIntensities(final double[] mzs, final double[] intensities) {
    if(mzs.length != intensities.length) {
      throw new IllegalArgumentException("m/z and intensity array lengths do not match");
    }
    final double[] peaks = new double[mzs.length * 2];
    for(int i = 0; i < mzs.length; i++) {
      peaks[i * 2] = mzs[i];
      peaks[i * 2 + 1] = intensities[i];
    }
    return peaks;
  }

  public Scan(final int msLevel, final int index, final int number, final double[] mzs, final double[] intensities) {
    this(msLevel, index, number, mergeMzAndIntensities(mzs, intensities));
  }

  public Scan clone() {
    try {
      final Scan clonedScan = (Scan) super.clone();
      return clonedScan;
    } catch(CloneNotSupportedException e) {
      throw new IllegalStateException(e);
    }
  }

  public double[] getPeaks() {
    return peaks;
  }

  public Iterator<Peak> iterator() {
    return getPeakObjects().iterator();
  }

  public ImmutableList<Peak> getPeakObjects() {
    final ImmutableList.Builder<Peak> peakListBuilder = ImmutableList.builder();
    for(int i = 0; i < peaks.length; i += 2) {
      peakListBuilder.add(buildPeak(peaks[i], peaks[i + 1]));
    }
    return peakListBuilder.build();
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
  public double getPrecursorMz() {
    return precursorMz;
  }

  public void setPeaks(final double[] peaks) {
    this.peaks = peaks;
  }

  public void setPrecursorMz(final double precursorMz) {
    this.precursorMz = precursorMz;
  }

  /**
   * @return The precursorIntensity of this scan, or 0 if it is unknown.
   */
  public double getPrecursorIntensity() {
    return precursorIntensity;
  }

  public void setPrecursorIntensity(final double precursorIntensity) {
    this.precursorIntensity = precursorIntensity;
  }

  /**
   * 
   * @return true if parent file name was explicitly defined in source file, false
   *         if it was inferred.
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

  /**
   * Set retention time for this scan.
   * 
   * @param rt
   *          target retention time (in milliseconds)
   */
  public void setRt(final long rt) {
    this.rt = rt;
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

  public Optional<Peak> mostIntensePeak(final double fromMz, final double toMz) {
    Peak mostIntensePeakFound = null;
    for(final Peak peak : this) {
      if(peak.onMzRange(fromMz, toMz)) {
        if(mostIntensePeakFound == null || peak.moreIntense(mostIntensePeakFound)) {
          mostIntensePeakFound = peak;
        }
      }
    }
    return Optional.fromNullable(mostIntensePeakFound);
  }

  private Peak buildPeak(final double mz, final double intensity) {
    return new Peak(mz, intensity, this);
  }

  public static class Peak implements Cloneable {
    private final double intensity;
    private final double mz;
    private final Scan scan;

    public Peak(final double mz, final double intensity, Scan scan) {
      this.intensity = intensity;
      this.mz = mz;
      this.scan = scan;
    }

    public boolean onMzRange(final double fromMz, final double toMz) {
      return fromMz <= mz && toMz >= mz;
    }

    public double getIntensity() {
      return intensity;
    }

    public double getMz() {
      return mz;
    }

    public Scan getScan() {
      return scan;
    }

    public boolean moreIntense(final Peak other) {
      return this.intensity > other.intensity;
    }

  }

  public int getIndex() {
    return index;
  }

}