/*******************************************************************************
 * Copyright 2009 Regents of the University of Minnesota. All rights
 * reserved.
 * Copyright 2009 Mayo Foundation for Medical Education and Research.
 * All rights reserved.
 *
 * This program is made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS
 * OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A
 * PARTICULAR PURPOSE.  See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 ******************************************************************************/

package edu.umn.msi.tropix.models.utils;

import java.util.LinkedList;
import java.util.List;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.Analysis;
import edu.umn.msi.tropix.models.BowtieAnalysis;
import edu.umn.msi.tropix.models.BowtieIndex;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.ITraqQuantitationAnalysis;
import edu.umn.msi.tropix.models.ITraqQuantitationTraining;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.IdentificationParameters;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.Note;
import edu.umn.msi.tropix.models.Parameters;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.Run;
import edu.umn.msi.tropix.models.Sample;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.models.TissueSample;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;

public class VisitorTest {

  static class RecordingTropixObjectVisitorImpl implements TropixObjectVisitor {
    private final List<TropixObjectTypeEnum> recordedTypes = new LinkedList<TropixObjectTypeEnum>();

    public void visitAnalysis(final Analysis analysis) {
      recordedTypes.add(TropixObjectTypeEnum.ANALYSIS);
    }

    public void visitBowtieAnalysis(final BowtieAnalysis bowtieAnalysis) {
      recordedTypes.add(TropixObjectTypeEnum.BOWTIE_ANALYSIS);
    }

    public void visitBowtieIndex(final BowtieIndex bowtieIndex) {
      recordedTypes.add(TropixObjectTypeEnum.BOWTIE_INDEX);
    }

    public void visitDatabase(final Database database) {
      recordedTypes.add(TropixObjectTypeEnum.DATABASE);
    }

    public void visitFolder(final Folder folder) {
      recordedTypes.add(TropixObjectTypeEnum.FOLDER);
    }

    public void visitITraqQuantitationAnalysis(final ITraqQuantitationAnalysis iTraqQuantitationAnalysis) {
      recordedTypes.add(TropixObjectTypeEnum.ITRAQ_QUANTITATION_ANALYSIS);
    }

    public void visitITraqQuantitationTraining(final ITraqQuantitationTraining iTraqQuantitationTraining) {
      recordedTypes.add(TropixObjectTypeEnum.ITRAQ_QUANTITATION_TRAINING);
    }

    public void visitIdentificationAnalysis(final IdentificationAnalysis identificationAnalysis) {
      recordedTypes.add(TropixObjectTypeEnum.PROTEIN_IDENTIFICATION_ANALYSIS);
    }

    public void visitIdentificationParameters(final IdentificationParameters identificationParameters) {
      recordedTypes.add(TropixObjectTypeEnum.IDENTIFICATION_PARAMETERS);
    }

    public void visitInternalRequest(final InternalRequest internalRequest) {
      recordedTypes.add(TropixObjectTypeEnum.INTERNAL_REQUEST);
    }

    public void visitNote(final Note note) {
      recordedTypes.add(TropixObjectTypeEnum.NOTE);
    }

    public void visitParameters(final Parameters parameters) {
      recordedTypes.add(TropixObjectTypeEnum.PARAMETERS);
    }

    public void visitProteomicsRun(final ProteomicsRun proteomicsRun) {
      recordedTypes.add(TropixObjectTypeEnum.PROTEOMICS_RUN);
    }

    public void visitRequest(final Request request) {
      recordedTypes.add(TropixObjectTypeEnum.REQUEST);
    }

    public void visitRun(final Run run) {
      recordedTypes.add(TropixObjectTypeEnum.RUN);
    }

    public void visitSample(final Sample sample) {
      recordedTypes.add(TropixObjectTypeEnum.SAMPLE);
    }

    public void visitScaffoldAnalysis(final ScaffoldAnalysis scaffoldAnalysis) {
      recordedTypes.add(TropixObjectTypeEnum.SCAFFOLD_ANALYSIS);
    }

    public void visitTissueSample(final TissueSample sample) {
      recordedTypes.add(TropixObjectTypeEnum.TISSUE_SAMPLE);
    }

    public void visitTropixFile(final TropixFile tropixFile) {
      recordedTypes.add(TropixObjectTypeEnum.FILE);
    }

    public void visitTropixObject(final TropixObject tropixObject) {
      recordedTypes.add(TropixObjectTypeEnum.TROPIX_OBJECT);
    }

    public void visitVirtualFolder(final VirtualFolder virtualFolder) {
      recordedTypes.add(TropixObjectTypeEnum.VIRTUAL_FOLDER);
    }

  }

  @Test(groups = "unit")
  public void testVisitInheritence() throws Exception {
    for(TropixObjectTypeEnum type : TropixObjectTypeEnum.values()) {
      final TropixObject instance = (TropixObject) Class.forName(type.getClassName()).newInstance();
      final RecordingTropixObjectVisitorImpl visitor = new RecordingTropixObjectVisitorImpl();
      TropixObjectVistorUtils.visit(instance, visitor);
      while(type != null) {
        assert visitor.recordedTypes.contains(type);
        type = type.getParentType();
      }
    }
  }

  @Test(groups = "unit")
  public void testDefaultImplementation() throws Exception {
    for(final TropixObjectTypeEnum type : TropixObjectTypeEnum.values()) {
      final TropixObject instance = (TropixObject) Class.forName(type.getClassName()).newInstance();
      TropixObjectVistorUtils.visit(instance, new TropixObjectVisitorImpl());
    }
  }

  @Test(groups = "unit")
  public void testUtilsConstructor() {
    new TropixObjectVistorUtils();
  }
}
