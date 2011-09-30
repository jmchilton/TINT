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

package edu.umn.msi.tropix.webgui.client.modules;

import edu.umn.msi.tropix.client.services.Constants;
import edu.umn.msi.tropix.webgui.client.ConfigurationOptions;
import edu.umn.msi.tropix.webgui.client.Session;
import edu.umn.msi.tropix.webgui.client.constants.ProtipConstants;
import edu.umn.msi.tropix.webgui.client.models.NewItemContext;
import edu.umn.msi.tropix.webgui.client.models.NewItemModelImpl;
import edu.umn.msi.tropix.webgui.services.session.Module;

public class ProtipModuleInstallerImpl extends BaseModuleInstallerImpl implements ModuleInstaller {
  private static final ProtipConstants CONSTANTS = ProtipConstants.INSTANCE;
  
  public void installNewItemModels(final Session session, final NewItemContext newItemContext) {
    newItemContext.addModel(new NewItemModelImpl(CONSTANTS.newIdentificationWorkflow(), "Upload raw files, run protein identification analyses, and merge the results into a Scaffold analysis."));
    newItemContext.addModel(new NewItemModelImpl(CONSTANTS.newProteomicsRun(), "Creates a proteomics runs from Thermo Finnigan RAW files or MzXML files."));
    final NewItemContext analysisContext = newItemContext.getChildContext("Analysis");
    analysisContext.addModel(new NewItemModelImpl(CONSTANTS.newIdentificationSearch(), "Creates an analysis by running a protein identification search engine, e.g. Sequest, X! Tandem, Mascot, or Omssa"));
    analysisContext.addModel(new NewItemModelImpl(CONSTANTS.newIdentificationSearchBatch(), "Creates multiple identification analyses from the runs associated with a sample."));
    analysisContext.addModel(new NewItemModelImpl(CONSTANTS.newPrerunIdentificationSearch(), "Creates an identification analysis from the uploaded output of a protein identification search engine."));
    if(session.getConfigurationOptionAsBoolean(ConfigurationOptions.serviceEnabled(Constants.SCAFFOLD))) {
      analysisContext.addModel(new NewItemModelImpl(CONSTANTS.newScaffoldAnalysis(), "Creates an analysis by combining other identification analyses using Scaffold."));
      analysisContext.addModel(new NewItemModelImpl(CONSTANTS.newScaffoldQPlusAnalysis(), "Creates an analysis by combining other identification analyses using Scaffold Q+."));
      analysisContext.addModel(new NewItemModelImpl(CONSTANTS.newScaffoldAnalysisBatch(), "Creates an analysis by combining other identification analyses using Scaffold."));      
    }
    if(session.getConfigurationOptionAsBoolean(ConfigurationOptions.serviceEnabled(Constants.ITRAQ_QUANTIFICATION))) {
      analysisContext.addModel(new NewItemModelImpl(CONSTANTS.newLtqIQuantAnalysis(), "Creates an iTraq quantitation from a manually produced Scaffold report file."));
      analysisContext.addModel(new NewItemModelImpl(CONSTANTS.newLtqIQuantTraining(), "Creates a training dataset for iTraq quantitation from a manually produced Scaffold report file."));
    }
    if(session.getConfigurationOptionAsBoolean(ConfigurationOptions.serviceEnabled(Constants.ID_PICKER))) {
      analysisContext.addModel(new NewItemModelImpl(CONSTANTS.newIdPickerAnalysis(), "Creates an analysis by combining other identification analyses using IDPicker."));
    }
  }

  public Module getModule() {
    return Module.PROTIP;
  }
  
}

