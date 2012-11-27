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

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions.GroupType;

/**
 * Classes implementing this interface should build a List of ITraqMatch objects from the specified mzxml files and scaffold report file.
 * 
 * @author John Chilton
 * 
 */
interface ITraqMatchBuilder {

  @Immutable
  public static class ITraqMatchBuilderOptions {
    @Nonnull
    private final ImmutableList<ITraqLabel> iTraqLabels;
    @Nonnull
    private final GroupType groupType;
    private final int threads;

    @Nonnull
    public ImmutableList<ITraqLabel> getITraqLabels() {
      return iTraqLabels;
    }

    public ITraqMatchBuilderOptions(@Nonnull final Iterable<ITraqLabel> iTraqLabels, @Nonnull final GroupType groupType, final int threads) {
      this.iTraqLabels = ImmutableList.copyOf(iTraqLabels);
      this.groupType = groupType;
      this.threads = threads;
    }

    public GroupType getGroupType() {
      return groupType;
    }

    public int getThreads() {
      return threads;
    }

  }

  List<ITraqMatch> buildDataEntries(final Iterable<File> mzxmlInputs, final InputReport scaffoldReport, final ITraqMatchBuilderOptions options);

}