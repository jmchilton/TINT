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

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;

class ITraqLabels {

  public static final class ITraqRatio {
    @Nonnull
    private final ITraqLabel numerator, denominator;

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + denominator.hashCode();
      result = prime * result + numerator.hashCode();
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
      ITraqRatio other = (ITraqRatio) obj;
      if(!denominator.equals(other.denominator)) {
        return false;
      }
      if(!numerator.equals(other.numerator)) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "ITraqRatio [denominator=" + denominator + ", numerator=" + numerator + "]";
    }

    private ITraqRatio(@Nonnull final ITraqLabel numerator, @Nonnull final ITraqLabel denominator) {
      this.numerator = numerator;
      this.denominator = denominator;
    }

    public ITraqLabel getNumerator() {
      return numerator;
    }

    public ITraqLabel getDenominator() {
      return denominator;
    }

  }

  private static enum ITraqLabelEnum implements ITraqLabel {
    // TODO: These should be centered around more accurate numbers.
    I113("i113", 113.0), I114("i114", 114.0), I115("i115", 115.0), I116("i116", 116.0),
    I117("i117", 117.0), I118("i118", 118.0), I119("i119", 119.0), I121("i121", 121.0),
    /*
     * 
     * reporter_mass_exact[113] = 113.1078;
     * reporter_mass_exact[114] = 114.1112;
     * reporter_mass_exact[115] = 115.1082;
     * reporter_mass_exact[116] = 116.1116;
     * reporter_mass_exact[117] = 117.1149;
     * reporter_mass_exact[118] = 118.1120;
     * reporter_mass_exact[119] = 119.1153;
     * reporter_mass_exact[121] = 121.1220;
     * 
     * reporter_mass_exact[126] = 126.127725;
     * reporter_mass_exact[127] = 127.124760;
     * reporter_mass_exact[128] = 128.134433;
     * reporter_mass_exact[129] = 129.131468;
     * reporter_mass_exact[130] = 130.141141;
     * reporter_mass_exact[131] = 131.138176;
     */
    I126("i126", 126.1), I127("i127", 127.1), I128("i128", 128.1),
    I129("i129", 129.1), I130("i130", 130.1), I131("i131", 131.1);

    private final String label;
    private final double lowerBound, upperBound;

    private ITraqLabelEnum(final String label, final double midPoint) {
      this.label = label;
      this.lowerBound = midPoint - 0.5;
      this.upperBound = midPoint + 0.5;
    }

    public String getLabel() {
      return label;
    }

    public boolean fitsPeak(final double mtoz) {
      return mtoz >= lowerBound && mtoz < upperBound;
    }

  }

  private static final ImmutableList<ITraqLabel> FOUR_PLEX_LABELS =
      ImmutableList.<ITraqLabel>builder().add(ITraqLabelEnum.I114).add(ITraqLabelEnum.I115).add(ITraqLabelEnum.I116).add(ITraqLabelEnum.I117).build();

  private static final ImmutableList<ITraqLabel> EIGHT_PLEX_LABELS =
      ImmutableList.<ITraqLabel>builder().add(ITraqLabelEnum.I113).add(ITraqLabelEnum.I114).add(ITraqLabelEnum.I115).add(ITraqLabelEnum.I116)
          .add(ITraqLabelEnum.I117).add(ITraqLabelEnum.I118).add(ITraqLabelEnum.I119).add(ITraqLabelEnum.I121).build();

  private static final ImmutableList<ITraqLabel> TMT_SIX_PLEX_LABELS =
      ImmutableList.<ITraqLabel>builder().add(ITraqLabelEnum.I126).add(ITraqLabelEnum.I127).add(ITraqLabelEnum.I128)
          .add(ITraqLabelEnum.I129).add(ITraqLabelEnum.I130).add(ITraqLabelEnum.I131).build();

  private static final ImmutableList<ITraqLabel> TMT_TWO_PLEX_LABELS =
      ImmutableList.<ITraqLabel>builder().add(ITraqLabelEnum.I126).add(ITraqLabelEnum.I127).build();

  public static ImmutableList<ITraqLabel> get4PlexLabels() {
    return FOUR_PLEX_LABELS;
  }

  public static ImmutableList<ITraqLabel> get8PlexLabels() {
    return EIGHT_PLEX_LABELS;
  }

  public static ImmutableList<ITraqLabel> getLabels(final QuantificationType type) {
    ImmutableList<ITraqLabel> labels;
    if(type == QuantificationType.FOUR_PLEX) {
      labels = FOUR_PLEX_LABELS;
    } else if(type == QuantificationType.EIGHT_PLEX) {
      labels = EIGHT_PLEX_LABELS;
    } else if(type == QuantificationType.TMT_TWO_PLEX) {
      labels = TMT_TWO_PLEX_LABELS;
    } else if(type == QuantificationType.TMT_SIX_PLEX) {
      labels = TMT_SIX_PLEX_LABELS;
    } else {
      throw new IllegalArgumentException("Unknown quantification type " + type);
    }
    return labels;
  }

  public static ImmutableList<ITraqRatio> buildRatios(final Iterable<ITraqLabel> labels) {
    final List<ITraqLabel> labelList = Lists.newArrayList(labels); // Convert arbitrary Iterable to ArrayList for O(1) index access
    final int numLabels = labelList.size();

    final ImmutableList.Builder<ITraqRatio> ratioListBuilder = ImmutableList.builder();
    for(int i = 0; i < numLabels; i++) {
      final ITraqLabel numerator = labelList.get(i);
      for(int j = i + 1; j < numLabels; j++) {
        final ITraqLabel denominator = labelList.get(j);
        ratioListBuilder.add(new ITraqRatio(numerator, denominator));
      }
    }
    return ratioListBuilder.build();
  }

}
