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

package edu.umn.msi.tropix.webgui.client.mediators;

import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.umn.msi.tropix.webgui.client.components.LocationCommandComponentFactory;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.constants.GenetipConstants;
import edu.umn.msi.tropix.webgui.client.constants.ProtipConstants;
import edu.umn.msi.tropix.webgui.client.mediators.ActionMediator.ActionEvent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;

// TODO: Move this functionality into module installers...
public class NewItemActionController {
  private final ActionMediator actionMediator;
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;
  private static final GenetipConstants GENETIP_CONSTANTS = GenetipConstants.INSTANCE;
  private static final ProtipConstants PROTIP_CONSTANTS = ProtipConstants.INSTANCE;

  @Inject
  public NewItemActionController(final ActionMediator actionMediator) {
    this.actionMediator = actionMediator;
  }

  private void registerListener(final String actionType, final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    actionMediator.registerActionListener("newItem" + actionType, new Listener<ActionEvent>() {
      public void onEvent(final ActionEvent event) {
        final LocationActionEventImpl locationActionEvent = (LocationActionEventImpl) event;
        commandComponentFactory.get(locationActionEvent.getItems()).execute();
      }
    });
  }

  @Inject
  public void setFolderCommandComponentFactory(@Named("folder") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(CONSTANTS.newFolder(), commandComponentFactory);
  }

  @Inject
  public void setSequenceDatabaseCommandComponentFactory(
      @Named("sequenceDatabase") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(CONSTANTS.newSequenceDatabase(), commandComponentFactory);
  }

  @Inject
  public void setArbitraryFileCommandComponentFactory(
      @Named("arbitraryFile") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(CONSTANTS.newGenericFile(), commandComponentFactory);
  }

  @Inject
  public void setWikiNoteCommandComponentFactory(@Named("wikiNote") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(CONSTANTS.newWikiNote(), commandComponentFactory);
  }

  @Inject
  public void setTissueSampleCommandComponentFactory(
      @Named("tissueSample") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener("Tissue Sample", commandComponentFactory);
  }

  @Inject
  public void setProteomicsRunCommandComponentFactory(
      @Named("proteomicsRun") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(PROTIP_CONSTANTS.newProteomicsRun(), commandComponentFactory);
  }

  @Inject
  public void setIdentificationWorkflowCommandComponentFactory(
      @Named("identificationWorkflow") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(PROTIP_CONSTANTS.newIdentificationWorkflow(), commandComponentFactory);
  }

  @Inject
  public void setIdentificationCommandComponentFactory(
      @Named("identificationAnalysis") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(PROTIP_CONSTANTS.newIdentificationSearch(), commandComponentFactory);
  }

  @Inject
  public void setBatchIdentificationCommandComponentFactory(
      @Named("batchIdentificationAnalysis") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(PROTIP_CONSTANTS.newIdentificationSearchBatch(), commandComponentFactory);
  }

  @Inject
  public void setUploadIdentificationAnalysisCommandComponentFactory(
      @Named("uploadIdentificationAnalysis") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(PROTIP_CONSTANTS.newPrerunIdentificationSearch(), commandComponentFactory);
  }

  @Inject
  public void setScaffoldAnalysisCommandComponentFactory(
      @Named("scaffoldAnalysis") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(PROTIP_CONSTANTS.newScaffoldAnalysis(), commandComponentFactory);
  }

  @Inject
  public void setScaffoldQPlusAnalysisCommandComponentFactory(
      @Named("scaffoldQPlusAnalysis") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(PROTIP_CONSTANTS.newScaffoldQPlusAnalysis(), commandComponentFactory);
  }

  @Inject
  public void setBatchScaffoldAnalysisCommandComponentFactory(
      @Named("batchScaffoldAnalysis") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(PROTIP_CONSTANTS.newScaffoldAnalysisBatch(), commandComponentFactory);
  }

  @Inject
  public void setIdPickerAnalysisCommandComponentFactory(
      @Named("idPickerAnalysis") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(PROTIP_CONSTANTS.newIdPickerAnalysis(), commandComponentFactory);
  }

  @Inject
  public void setITraqQuantificationAnalysisCommandComponentFactory(
      @Named("iTraqQuantificationAnalysis") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(PROTIP_CONSTANTS.newLtqIQuantAnalysis(), commandComponentFactory);
  }

  @Inject
  public void setITraqQuantificationTrainingCommandComponentFactory(
      @Named("iTraqQuantificationTraining") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(PROTIP_CONSTANTS.newLtqIQuantTraining(), commandComponentFactory);
  }

  @Inject
  public void setBowtieAnalysisCommandComponentFactory(
      @Named("bowtieAnalysis") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(GENETIP_CONSTANTS.newBowtieAnalysis(), commandComponentFactory);
  }

  @Inject
  public void setUploadBowtieIndexCommandComponentFactory(
      @Named("bowtieIndex") final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    registerListener(GENETIP_CONSTANTS.newBowtieIndex(), commandComponentFactory);
  }

}
