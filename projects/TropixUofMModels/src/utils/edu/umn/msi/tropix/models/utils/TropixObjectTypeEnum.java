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

/**
 * This is meant to replace Java class literals as mechanism to communicate type information across various API boundaries that class literals cannot go. Mainly from GWT to the server, but also from outside the persistence layer to inside.
 * 
 * @author John Chilton
 * 
 */
public enum TropixObjectTypeEnum implements TropixObjectType {
  TROPIX_OBJECT(TropixObject.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof TropixObject;
    }
  }, null), SAMPLE(Sample.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof Sample;
    }
  }, TROPIX_OBJECT), TISSUE_SAMPLE(TissueSample.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof TissueSample;
    }
  }, SAMPLE), FOLDER(Folder.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof Folder;
    }
  }, TROPIX_OBJECT), VIRTUAL_FOLDER(VirtualFolder.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof VirtualFolder;
    }
  }, TROPIX_OBJECT), FILE(TropixFile.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof TropixFile;
    }
  }, TROPIX_OBJECT), DATABASE(Database.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof Database;
    }
  }, TROPIX_OBJECT), ANALYSIS(Analysis.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof Analysis;
    }
  }, TROPIX_OBJECT), PROTEIN_IDENTIFICATION_ANALYSIS(IdentificationAnalysis.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof IdentificationAnalysis;
    }
  }, ANALYSIS), SCAFFOLD_ANALYSIS(ScaffoldAnalysis.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof ScaffoldAnalysis;
    }
  }, ANALYSIS), PARAMETERS(Parameters.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof Parameters;
    }
  }, TROPIX_OBJECT), IDENTIFICATION_PARAMETERS(IdentificationParameters.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof IdentificationParameters;
    }
  }, PARAMETERS), RUN(Run.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof Run;
    }
  }, TROPIX_OBJECT), PROTEOMICS_RUN(ProteomicsRun.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof ProteomicsRun;
    }
  }, RUN), NOTE(Note.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof Note;
    }
  }, TROPIX_OBJECT), REQUEST(Request.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof Request;
    }
  }, TROPIX_OBJECT), INTERNAL_REQUEST(InternalRequest.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof InternalRequest;
    }
  }, REQUEST), ITRAQ_QUANTITATION_TRAINING(ITraqQuantitationTraining.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof ITraqQuantitationTraining;
    }
  }, TROPIX_OBJECT), ITRAQ_QUANTITATION_ANALYSIS(ITraqQuantitationAnalysis.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof ITraqQuantitationAnalysis;
    }
  }, ANALYSIS), BOWTIE_ANALYSIS(BowtieAnalysis.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof BowtieAnalysis;
    }
  }, ANALYSIS), BOWTIE_INDEX(BowtieIndex.class.getName(), new IsA() {
    public boolean isInstance(final Object object) {
      return object instanceof BowtieIndex;
    }
  }, TROPIX_OBJECT);

  // Used to implement isInstance method, which is an ugly kludge
  // to circumvent the fact that Class.isInstance is not available
  // in GWT.
  private interface IsA {
    boolean isInstance(Object object);
  }

  private final String className;
  private final TropixObjectTypeEnum parentType;
  private final IsA isA;

  private TropixObjectTypeEnum(final String className, final IsA isA, final TropixObjectTypeEnum parentType) {
    this.className = className;
    this.parentType = parentType;
    this.isA = isA;
  }

  /**
   * @param object Object to check type of.
   * @return true iff the given object is an instance of of the type encapsulated by this instance of of the enum.
   */
  public boolean isInstance(final TropixObject object) {
    return isA.isInstance(object);
  }

  /**
   * 
   * @return The fully qualified Java class name corresponding to the type represented by this enum.
   */
  public String getClassName() {
    return className;
  }

  /**
   * @return The parent type of this object, or null if called on TROPIX_OBJECT.
   */
  public TropixObjectTypeEnum getParentType() {
    return parentType;
  }

  /**
   * 
   * @param tropixObject Object to obtain TropixObjectTypeEnum instance for
   * @return The TropixObjectTypeEnum enumerated value which most tightly describes the type of the given object.
   */
  public static TropixObjectTypeEnum getType(final TropixObject tropixObject) {
    final TropixObjectTypeEnum[] typesInOrder = values();

    TropixObjectTypeEnum result = null;

    // Work backwards, because each subtype must appear after any super
    // types, so the tightest type bound will always be latter in the
    // array.
    for(int i = typesInOrder.length - 1; i >= 0; i--) {
      final TropixObjectTypeEnum type = typesInOrder[i];
      if(type.isInstance(tropixObject)) {
        result = type;
        break;
      }
    }
    return result;
  }

}
