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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.internal.Iterables;

@Immutable
class ScanIndex {
  private final String name;
  private final Integer fileIndex;
  private final int number;
  private final short charge;
  private final ImmutableSet<String> alternativeNames;
  private static final Pattern MULTIFILE_PATTERN = Pattern.compile("^\\d+_task_(.*)\\.dat$");

  ScanIndex(final String name, final Integer fileIndex, final int number, final short charge) {
    this(name, fileIndex, number, charge, Lists.<String>newArrayList(name));
  }

  ScanIndex(final String name, final Integer fileIndex, final int number, final short charge, Iterable<String> alternativeNames) {
    this.name = name;
    this.fileIndex = fileIndex;
    this.number = number;
    this.charge = charge;
    this.alternativeNames = buildAlternativeNames(alternativeNames);
  }

  private static final ImmutableSet<String> buildAlternativeNames(final Iterable<String> alternativeNames) {
    final ImmutableSet.Builder<String> alternativeNamesBuilder = ImmutableSet.builder();
    for(final String alternativeName : alternativeNames) {
      alternativeNamesBuilder.add(alternativeName);
      final Matcher matcher = MULTIFILE_PATTERN.matcher(alternativeName);
      if(matcher.matches()) {
        alternativeNamesBuilder.add(matcher.group(1));
      }
    }
    return alternativeNamesBuilder.build();
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

  public int getNumber() {
    return number;
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
    if(fileIndex != null && other.fileIndex != null && fileIndex != other.fileIndex) {
      return false;
    }
    return true;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    String altNameStr = "";
    if(alternativeNames.size() > 1) {
      altNameStr = ", alternativeNames=" + Iterables.toString(alternativeNames);
    }
    return "ScanIndex [charge=" + charge + ", name=" + name + ", number=" + number + altNameStr + ",fileIndex=" + fileIndex + "]";
  }

  public boolean numberAndChargeMatch(@Nonnull final ScanIndex scanIndex) {
    return scanIndex.number == number && (scanIndex.charge == 0 || scanIndex.charge == charge);
  }

  public boolean numberAndNameMatch(@Nonnull final ScanIndex scanIndex) {
    final boolean numberAndNameMatch = (scanIndex.number == number) && namesMatch(scanIndex);
    if(numberAndNameMatch) {
      System.out.println("Number and Name Match: " + numberAndNameMatch + " for " + scanIndex + " and " + this);
    }
    return numberAndNameMatch;
  }

  private boolean namesMatch(@Nonnull final ScanIndex scanIndex) {
    final boolean namesMatch = !Sets.intersection(alternativeNames, scanIndex.alternativeNames).isEmpty();
    return namesMatch;
  }

  public boolean numberAndFileIndexMatch(@Nonnull final ScanIndex scanIndex) {
    boolean match = false;
    if(scanIndex.number == number) {
      match = fileIndex != null && fileIndex.equals(scanIndex.fileIndex);
    }
    return match;
  }

  public boolean numberChargeAndAlternativeNameMatch(@Nonnull final ScanIndex scanIndex) {
    boolean match = false;
    if(numberAndChargeMatch(scanIndex)) {
      match = !Sets.intersection(alternativeNames, scanIndex.alternativeNames).isEmpty();
    }
    return match;
  }

}