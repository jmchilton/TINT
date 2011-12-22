package edu.umn.msi.tropix.webgui.client.components.newwizards;

import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.webgui.client.components.UploadComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponent.CanUpload;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;

public class FileSourceWizardPageImpl extends WizardPageImpl<VLayout> {
  private final UploadComponent uploadComponent;
  
  public FileSourceWizardPageImpl(final UploadComponent uploadComponent) {
    this.uploadComponent = uploadComponent;
  }
  
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

  
}
