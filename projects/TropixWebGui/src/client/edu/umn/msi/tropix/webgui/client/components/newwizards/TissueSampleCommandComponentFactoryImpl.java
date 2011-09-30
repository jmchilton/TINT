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

package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.ArrayList;
import java.util.Collection;

import com.smartgwt.client.widgets.Window;

import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTissueSampleDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponent;
import edu.umn.msi.tropix.webgui.client.components.newwizards.MetadataWizardPageFactory.MetadataWizardPageImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.utils.Sets;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardCompletionHandler;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardOptions;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPage;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;

public class TissueSampleCommandComponentFactoryImpl extends WizardCommandComponentFactoryImpl {
  @Override
  public WizardCommand get(final Collection<TreeItem> locations) {
    return new WizardCommand(locations) {
      public void execute() {
        final ArrayList<WizardPage> pages = new ArrayList<WizardPage>(1);
        final MetadataWizardPageImpl metadataWizardPage = getMetadataWizardPageFactory().get(getLocations(), "tissue sample");
        pages.add(metadataWizardPage);
        final WizardOptions options = new WizardOptions();
        options.setTitle("New Tissue Sample");
        WizardFactoryImpl.getInstance().getWizard(pages, options, new WizardCompletionHandler() {
          public void onCompletion(final Window wizard) {
            final MetadataInputComponent metadataSupplier = metadataWizardPage.getMetadataCanvasSupplier();
            final JobDescription jobDescription = new JobDescription("Create tissue sample " + metadataWizardPage.getMetadataCanvasSupplier().getName());
            final CreateTissueSampleDescription description = new CreateTissueSampleDescription();
            description.setJobDescription(jobDescription);
            ActivityDescriptions.initCommonMetadata(description, metadataSupplier);            
            description.setCommitted(true);
            JobSubmitService.Util.getInstance().submit(Sets.<ActivityDescription>newHashSet(description), new AsyncCallbackImpl<Void>());
            wizard.destroy();
          }
        });
      }
    };
  }
}
