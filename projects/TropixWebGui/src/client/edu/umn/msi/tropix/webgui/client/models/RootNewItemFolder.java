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

import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;

public class RootNewItemFolder extends NewItemFolderImpl {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;

  public RootNewItemFolder() {
    super("Root", "Root new folder");
    addModel(new NewItemModelImpl(CONSTANTS.newFolder(), "A folder containing tropix objects, e.g. samples, runs, analyses, and other folders."));
    addModel(new NewItemModelImpl(CONSTANTS.newSequenceDatabase(), "A sequence database. See http://www.ncbi.nlm.nih.gov/blast/fasta.shtml for more information."));
    final NewItemFolderImpl otherFolder = new NewItemFolderImpl("Other", null);
    otherFolder.addModel(new NewItemModelImpl(CONSTANTS.newGenericFile(), "An arbitrary file. Warning: This file will have no type information associated with it, so you will not be able to run Tropix analytical services on this file."));
    otherFolder.addModel(new NewItemModelImpl(CONSTANTS.newWikiNote(), "A user modifiable note that can be edited using the Mediawiki markup language."));
    addModel(otherFolder);
  }

}
