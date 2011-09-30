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

package edu.umn.msi.tropix.webgui.client.models;

import com.google.common.base.Function;

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
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.constants.ProtipConstants;

public class ModelFunctions {
  private static final ProtipConstants CONSTANTS = ProtipConstants.INSTANCE;

  public static Function<TropixObject, String> getTypeFunction() {
    return ModelFunctions.TYPE_FUNCTION;
  }

  private static final Function<TropixObject, String> TYPE_FUNCTION = new Function<TropixObject, String>() {
    public String apply(final TropixObject object) {
      String type = "Object";
      if(object instanceof ScaffoldAnalysis) {
        type = CONSTANTS.newScaffoldAnalysis();
      } else if(object instanceof IdentificationAnalysis) {
        type = CONSTANTS.newIdentificationSearch();
      } else if(object instanceof Folder) {
        type = "Folder";
      } else if(object instanceof ITraqQuantitationAnalysis) {
        type = CONSTANTS.newLtqIQuantAnalysis();
      } else if(object instanceof Database) {
        type = "Sequence Database";
      } else if(object instanceof VirtualFolder) {
        type = "Shared Folder";
      } else if(object instanceof IdentificationParameters) {
        type = "Identification Parameters";
      } else if(object instanceof ProteomicsRun) {
        type = CONSTANTS.typePeakList();
      } else if(object instanceof Sample) {
        type = "Sample";
      } else if(object instanceof TropixFile) {
        type = "File";
      } else if(object instanceof Request) {
        type = "Request";
      } else if(object instanceof Note) {
        type = "Wiki Note";
      } else if(object instanceof BowtieIndex) {
        type = "Bowtie Index";
      } else if(object instanceof BowtieAnalysis) {
        type = "Bowtie Analysis";
      } else if(object instanceof ITraqQuantitationTraining) {
        type = CONSTANTS.newLtqIQuantTraining();
      }
      return type;
    }
  };

  public static Function<TropixObject, String> getIconFunction16() {
    return ModelFunctions.ICON_FUNCTION_16;
  }

  static final Function<TropixObject, String> ICON_FUNCTION_16 = new Function<TropixObject, String>() {
    public String apply(final TropixObject object) {
      String iconUrl = Resources.OBJECT_16;
      if(object instanceof Analysis) {
        iconUrl = Resources.ANALYSIS_16;
      } else if(object instanceof Folder) {
        iconUrl = Resources.FOLDER_16;
      } else if(object instanceof Database) {
        iconUrl = Resources.DATABASE_16;
      } else if(object instanceof VirtualFolder) {
        iconUrl = Resources.SHARED_FOLDER_16;
      } else if(object instanceof Parameters) {
        iconUrl = Resources.PARAMETERS_16;
      } else if(object instanceof Run) {
        iconUrl = Resources.RUN_16;
      } else if(object instanceof Sample) {
        iconUrl = Resources.SAMPLE_16;
      } else if(object instanceof TropixFile) {
        iconUrl = Resources.FILE_16;
      } else if(object instanceof Request) {
        iconUrl = Resources.REQUEST_16;
      } else if(object instanceof Note) {
        iconUrl = Resources.NOTE_16;
      } else if(object instanceof BowtieIndex) {
        iconUrl = Resources.DATABASE_16;
      } else if(object instanceof ITraqQuantitationTraining) {
        iconUrl = Resources.ANALYSIS_16;
      }
      return iconUrl;
    }
  };
}
