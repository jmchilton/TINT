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

public class TropixObjectVistorUtils {
  public static void visit(final TropixObject tropixObject, final TropixObjectVisitor visitor) {
    visitor.visitTropixObject(tropixObject);
    if(tropixObject instanceof Sample) {
      visitor.visitSample((Sample) tropixObject);
      if(tropixObject instanceof TissueSample) {
        visitor.visitTissueSample((TissueSample) tropixObject);
      }
    } else if(tropixObject instanceof Run) {
      visitor.visitRun((Run) tropixObject);
      if(tropixObject instanceof ProteomicsRun) {
        visitor.visitProteomicsRun((ProteomicsRun) tropixObject);
      }
    } else if(tropixObject instanceof Folder) {
      visitor.visitFolder((Folder) tropixObject);
    } else if(tropixObject instanceof Parameters) {
      visitor.visitParameters((Parameters) tropixObject);
      if(tropixObject instanceof IdentificationParameters) {
        visitor.visitIdentificationParameters((IdentificationParameters) tropixObject);
      }
    } else if(tropixObject instanceof Database) {
      visitor.visitDatabase((Database) tropixObject);
    } else if(tropixObject instanceof TropixFile) {
      visitor.visitTropixFile((TropixFile) tropixObject);
    } else if(tropixObject instanceof Analysis) {
      visitor.visitAnalysis((Analysis) tropixObject);
      if(tropixObject instanceof IdentificationAnalysis) {
        visitor.visitIdentificationAnalysis((IdentificationAnalysis) tropixObject);
      } else if(tropixObject instanceof ScaffoldAnalysis) {
        visitor.visitScaffoldAnalysis((ScaffoldAnalysis) tropixObject);
      } else if(tropixObject instanceof ITraqQuantitationAnalysis) {
        visitor.visitITraqQuantitationAnalysis((ITraqQuantitationAnalysis) tropixObject);
      } else if(tropixObject instanceof BowtieAnalysis) {
        visitor.visitBowtieAnalysis((BowtieAnalysis) tropixObject);
      }
    } else if(tropixObject instanceof BowtieIndex) {
      visitor.visitBowtieIndex((BowtieIndex) tropixObject);
    } else if(tropixObject instanceof VirtualFolder) {
      visitor.visitVirtualFolder((VirtualFolder) tropixObject);
    } else if(tropixObject instanceof Note) {
      visitor.visitNote((Note) tropixObject);
    } else if(tropixObject instanceof Request) {
      visitor.visitRequest((Request) tropixObject);
      if(tropixObject instanceof InternalRequest) {
        visitor.visitInternalRequest((InternalRequest) tropixObject);
      }
    } else if(tropixObject instanceof ITraqQuantitationTraining) {
      visitor.visitITraqQuantitationTraining((ITraqQuantitationTraining) tropixObject);
    }
  }
}
