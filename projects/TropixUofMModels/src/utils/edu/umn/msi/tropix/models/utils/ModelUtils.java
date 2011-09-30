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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.umn.msi.tropix.models.Analysis;
import edu.umn.msi.tropix.models.BowtieAnalysis;
import edu.umn.msi.tropix.models.BowtieIndex;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.ITraqQuantitationAnalysis;
import edu.umn.msi.tropix.models.ITraqQuantitationTraining;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.Run;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;

/**
 * This class stores generic utility methods used to operate on TropixObject instances.
 * 
 * @author John Chilton
 * 
 */
public class ModelUtils {

  private static void addAll(final Collection<TropixObject> to, final Iterable<? extends TropixObject> from) {
    if(from != null) {
      for(final TropixObject object : from) {
        to.add(object);
      }
    }
  }

  private static void add(final Collection<TropixObject> to, final TropixObject object) {
    if(object != null) {
      to.add(object);
    }
  }

  /**
   * @return True iff tropixObject is of a type which can have children (doesn't actually
   *         verify it has children).
   */
  public static boolean hasChildren(final TropixObject tropixObject) {
    return (tropixObject instanceof Folder
             || tropixObject instanceof VirtualFolder
             || tropixObject instanceof Request
             || tropixObject instanceof ProteomicsRun
             || tropixObject instanceof IdentificationAnalysis
             || tropixObject instanceof ScaffoldAnalysis
             || tropixObject instanceof ITraqQuantitationAnalysis
             || tropixObject instanceof BowtieAnalysis
             || tropixObject instanceof Database
             || tropixObject instanceof BowtieIndex || tropixObject instanceof ITraqQuantitationTraining);
  }

  /**
   * 
   * @param tropixObject
   *          Object to obtain children of.
   * @return A collection describing the children of the input object. For instance
   *         this will return any file associated by run or analyses objects and will return
   *         the contents of folders and virtual folders.
   */
  public static Collection<TropixObject> getChildren(final TropixObject tropixObject) {
    final List<TropixObject> children = new LinkedList<TropixObject>();
    if(tropixObject instanceof Folder) {
      addAll(children, ((Folder) tropixObject).getContents());
    } else if(tropixObject instanceof VirtualFolder) {
      addAll(children, ((VirtualFolder) tropixObject).getContents());
    } else if(tropixObject instanceof Request) {
      addAll(children, ((Request) tropixObject).getContents());
    } else if(tropixObject instanceof Run) {
      if(tropixObject instanceof ProteomicsRun) {
        final ProteomicsRun proteomicsRun = (ProteomicsRun) tropixObject;
        add(children, proteomicsRun.getMzxml());
        add(children, proteomicsRun.getSource());
      }
    } else if(tropixObject instanceof Analysis) {
      if(tropixObject instanceof IdentificationAnalysis) {
        final IdentificationAnalysis idAnalysis = (IdentificationAnalysis) tropixObject;
        add(children, idAnalysis.getOutput());
      } else if(tropixObject instanceof ScaffoldAnalysis) {
        final ScaffoldAnalysis scaffoldAnalysis = (ScaffoldAnalysis) tropixObject;
        add(children, scaffoldAnalysis.getInput());
        add(children, scaffoldAnalysis.getOutputs());
      } else if(tropixObject instanceof ITraqQuantitationAnalysis) {
        add(children, ((ITraqQuantitationAnalysis) tropixObject).getOutput());
        add(children, ((ITraqQuantitationAnalysis) tropixObject).getReport());
      } else if(tropixObject instanceof BowtieAnalysis) {
        add(children, ((BowtieAnalysis) tropixObject).getOutput());
      }
    } else if(tropixObject instanceof Database) {
      add(children, ((Database) tropixObject).getDatabaseFile());
    } else if(tropixObject instanceof ITraqQuantitationTraining) {
      add(children, ((ITraqQuantitationTraining) tropixObject).getReport());
      add(children, ((ITraqQuantitationTraining) tropixObject).getTrainingFile());
    } else if(tropixObject instanceof BowtieIndex) {
      add(children, ((BowtieIndex) tropixObject).getIndexesFile());
    }
    return children;
  }

  /**
   * 
   * @param tropixFile
   *          File to fetch the extension of.
   * @return The file extension associated with tropixFile including first period,
   *         so for instance moo.cow => .cow.
   */
  public static String getExtension(final TropixFile tropixFile) {
    final FileType fileType = tropixFile.getFileType();
    String extension = "";
    if(fileType != null) {
      extension = fileType.getExtension();
    }
    return extension;
  }

  /**
   * 
   * @param objects
   *          Objects to fetch ids from.
   * @return An {@link Iterable} over the ids in the input iterable.
   */
  public static Iterable<String> getIds(final Iterable<? extends TropixObject> objects) {
    final ArrayList<String> ids = new ArrayList<String>();
    for(final TropixObject object : objects) {
      ids.add(object.getId());
    }
    return ids;
  }

}
