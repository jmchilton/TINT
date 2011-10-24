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
import java.util.LinkedHashMap;

import com.google.inject.Inject;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.UploadFileDescription;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.FileTypeFormItemComponent;
import edu.umn.msi.tropix.webgui.client.components.FileTypeFormItemComponent.FileTypeFormItemOptions;
import edu.umn.msi.tropix.webgui.client.components.UploadComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponent.CanUpload;
import edu.umn.msi.tropix.webgui.client.components.UploadComponentFactory.UploadComponentOptions;
import edu.umn.msi.tropix.webgui.client.components.newwizards.MetadataWizardPageFactory.MetadataWizardPageImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.utils.Sets;
import edu.umn.msi.tropix.webgui.client.widgets.ItemWrapper;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardCompletionHandler;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardOptions;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPage;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;

public class ArbitraryFileUploadCommandComponentFactoryImpl extends WizardCommandComponentFactoryImpl {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;

  private class ArbitraryFileUploadWizardCommand extends WizardCommand {
    ArbitraryFileUploadWizardCommand(final Collection<TreeItem> locations) {
      super(locations);
    }

    private MetadataWizardPageImpl metadataPage = getMetadataWizardPageFactory().get(getLocations(), "file");
    private FileTypeFormItemComponent fileTypeComponent; // = fileTypeFormItemComponentFactory.get();

    private final UploadComponentOptions uploadOptions = new UploadComponentOptions(new AsyncCallbackImpl<LinkedHashMap<String, String>>() {
      @Override
      protected void handleSuccess() {
        final JobDescription jobDescription = new JobDescription("Create file " + metadataPage.getMetadataCanvasSupplier().getName());
        final UploadFileDescription uploadDescription = ActivityDescriptions.createUploadFileDescription(jobDescription, getResult().values()
            .iterator().next());

        
        final String modelName = metadataPage.getMetadataCanvasSupplier().getName();

        final CreateTropixFileDescription createFileDescription = ActivityDescriptions.createFileFromUpload(uploadDescription, true);
        ActivityDescriptions.initCommonMetadata(createFileDescription, metadataPage.getMetadataCanvasSupplier());
        if(fileTypeComponent.isAutoDetect()) {
          createFileDescription.setName(modelName);
        } else {
          final String extension = fileTypeComponent.getSelection().getExtension(); // name.contains(".") ? name.substring(name.lastIndexOf('.')) : "";
          createFileDescription.setName(modelName.toLowerCase().endsWith(extension.toLowerCase()) ? modelName : modelName + extension);
          createFileDescription.setExtension(extension);          
        }
        createFileDescription.setCommitted(true);

        JobSubmitService.Util.getInstance().submit(Sets.<ActivityDescription>newHashSet(uploadDescription, createFileDescription),
            new AsyncCallbackImpl<Void>());
        destroy();
      }
    });

    class UploadWizardPageImpl extends WizardPageImpl<VLayout> {

      @Override
      public boolean allowNext() {
        return true;
      }

      @Override
      public boolean isValid() {
        final CanUpload canUpload = uploadComponent.canUpload();
        setError(canUpload.getReason());
        return canUpload.getCanUpload();
      }

      UploadWizardPageImpl() {
        setTitle("File");
        setDescription("Specify a file to upload");
        
        final FileTypeFormItemOptions fileTypeOptions = new FileTypeFormItemOptions();
        fileTypeOptions.setAllowAutoDetect(true);
        //final Form fileTypeForm = new Form();
        fileTypeComponent = fileTypeFormItemComponentFactory.get(fileTypeOptions);
        final ItemWrapper wrapper = new ItemWrapper(fileTypeComponent.get());
        fileTypeComponent.setSelection("");
        final VLayout layout = new VLayout();
        layout.setHeight100();
        layout.setWidth100();
        this.setCanvas(layout);
        setValid(true);
        layout.addMember(wrapper);
        layout.addMember(uploadComponent.get());
      }
    }

    private UploadComponent uploadComponent = uploadComponentFactory.get(uploadOptions);
    private UploadWizardPageImpl uploadPage = new UploadWizardPageImpl();

    public void execute() {
      final ArrayList<WizardPage> pages = new ArrayList<WizardPage>(2);
      pages.add(metadataPage);
      pages.add(uploadPage);
      final WizardOptions options = new WizardOptions();
      options.setTitle(CONSTANTS.genericFileWizardTitle());
      setWidget(WizardFactoryImpl.getInstance().getWizard(pages, options, new WizardCompletionHandler() {
        public void onCompletion(final Window wizard) {
          uploadComponent.startUpload();
        }
      }));
    }
  }

  public WizardCommand get(final Collection<TreeItem> locations) {
    return new ArbitraryFileUploadWizardCommand(locations);
  }

  private ComponentFactory<UploadComponentOptions, ? extends UploadComponent> uploadComponentFactory;

  @Inject
  public void setUploadComponentFactory(final ComponentFactory<UploadComponentOptions, ? extends UploadComponent> uploadComponentFactory) {
    this.uploadComponentFactory = uploadComponentFactory;
  }

  private ComponentFactory<FileTypeFormItemComponent.FileTypeFormItemOptions, FileTypeFormItemComponent> fileTypeFormItemComponentFactory;

  @Inject
  public void setFileTypeFormItemComponentSupplier(final ComponentFactory<FileTypeFormItemComponent.FileTypeFormItemOptions, FileTypeFormItemComponent> fileTypeFormItemComponentFactory) {
    this.fileTypeFormItemComponentFactory = fileTypeFormItemComponentFactory;
  }

}
