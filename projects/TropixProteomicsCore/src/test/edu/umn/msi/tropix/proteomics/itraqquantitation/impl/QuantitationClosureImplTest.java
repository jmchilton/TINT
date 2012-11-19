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
import java.util.Collection;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions.QuantitationOptionsBuilder;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ITraqLabels.ITraqRatio;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ITraqMatchBuilder.ITraqMatchBuilderOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportParser.ReportType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.QuantificationResults;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.RatioLabel;

public class QuantitationClosureImplTest {
  private QuantitationClosureImpl closure;
  private MockObjectCollection mockObjects;
  private ITraqMatchBuilder matcher;
  private InputReport inputReport;
  private Quantifier quantifier;

  private final Iterable<File> inputMzxmlFiles = Lists.newArrayList(new File("test.mzxml"));
  private final File inputScaffoldFile = new File("test.xls");
  private File outputFile;

  private final Iterable<File> trainingMzxmlFiles = Lists.newArrayList(new File("train.mzxml"));
  private final File trainingScaffoldFile = new File("train.xls");

  private final List<ITraqMatch> inputMatches = Lists.newArrayList(), trainingMatches = Lists.newArrayList();
  private QuantitationOptionsBuilder optionsBuilder;

  @BeforeMethod(groups = "unit")
  public void init() {
    closure = new QuantitationClosureImpl();
    matcher = EasyMock.createMock(ITraqMatchBuilder.class);
    quantifier = EasyMock.createMock(Quantifier.class);
    closure.setItraqMatchBuilder(matcher);
    closure.setQuantifier(quantifier);
    outputFile = FileUtilsFactory.getInstance().createTempFile("moo", ".xml");

    inputReport = new InputReport(inputScaffoldFile, ReportType.SCAFFOLD);
    optionsBuilder = QuantitationOptions.forInput(inputMzxmlFiles, inputReport).withOutput(
        outputFile);
    mockObjects = MockObjectCollection.fromObjects(matcher, quantifier);
  }

  @AfterMethod(groups = "unit")
  public void tearDown() {
    FileUtilsFactory.getInstance().deleteQuietly(outputFile);
  }

  @Test(groups = "unit")
  public void apply4plexNoTraining() {
    final QuantitationOptions options = optionsBuilder.is4Plex().get();

    matcher.buildDataEntries(EasyMockUtils.<File, Collection<File>>hasSameUniqueElements(inputMzxmlFiles), EasyMock.eq(inputReport),
        matches(ITraqLabels.get4PlexLabels()));
    EasyMock.expectLastCall().andReturn(inputMatches);

    final QuantificationResults report = new QuantificationResults();
    final RatioLabel label = new RatioLabel();
    label.setNumeratorLabel("i114");
    label.setDenominatorLabel("i115");
    report.getRatioLabel().add(label);

    quantifier.quantify(
        EasyMockUtils.<ITraqRatio, Collection<ITraqRatio>>hasSameUniqueElements(ITraqLabels.buildRatios(ITraqLabels.get4PlexLabels())),
        EasyMock.isA(ReportSummary.class), (Function<Double, Double>) EasyMock.isNull());
    EasyMock.expectLastCall().andReturn(report);
    mockObjects.replay();
    closure.apply(options);
    mockObjects.verifyAndReset();
  }

  private ITraqMatchBuilderOptions matches(final Iterable<ITraqLabel> labels) {
    EasyMock.reportMatcher(new LabelMatcher(labels));
    return null;
  }

  private class LabelMatcher implements IArgumentMatcher {
    private final Iterable<ITraqLabel> labels;

    public LabelMatcher(final Iterable<ITraqLabel> labels) {
      this.labels = labels;
    }

    public void appendTo(final StringBuffer arg0) {
    }

    public boolean matches(final Object optionsObject) {
      final ITraqMatchBuilderOptions options = (ITraqMatchBuilderOptions) optionsObject;
      return Iterables.elementsEqual(options.getITraqLabels(), labels);
    }

  }

}
