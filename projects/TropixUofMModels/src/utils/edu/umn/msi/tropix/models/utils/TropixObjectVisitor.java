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

import edu.umn.msi.tropix.models.Analysis;
import edu.umn.msi.tropix.models.BowtieAnalysis;
import edu.umn.msi.tropix.models.BowtieIndex;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.ITraqQuantitationAnalysis;
import edu.umn.msi.tropix.models.ITraqQuantitationTraining;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.IdentificationParameters;
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

public interface TropixObjectVisitor {

  void visitTropixObject(TropixObject tropixObject);

  void visitSample(Sample sample);

  void visitTissueSample(TissueSample sample);

  void visitRun(Run run);

  void visitProteomicsRun(ProteomicsRun proteomicsRun);

  void visitAnalysis(Analysis analysis);

  void visitIdentificationAnalysis(IdentificationAnalysis identificationAnalysis);

  void visitScaffoldAnalysis(ScaffoldAnalysis scaffoldAnalysis);

  void visitTropixFile(TropixFile tropixFile);

  void visitDatabase(Database database);

  void visitFolder(Folder folder);

  void visitParameters(Parameters parameters);

  void visitIdentificationParameters(IdentificationParameters identificationParameters);

  void visitVirtualFolder(VirtualFolder virtualFolder);

  void visitNote(Note note);

  void visitRequest(Request request);

  void visitInternalRequest(edu.umn.msi.tropix.models.InternalRequest internalRequest);

  void visitITraqQuantitationTraining(ITraqQuantitationTraining iTraqQuantitationTraining);

  void visitITraqQuantitationAnalysis(ITraqQuantitationAnalysis iTraqQuantitationAnalysis);

  void visitBowtieAnalysis(BowtieAnalysis bowtieAnalysis);

  void visitBowtieIndex(BowtieIndex bowtieIndex);
}
