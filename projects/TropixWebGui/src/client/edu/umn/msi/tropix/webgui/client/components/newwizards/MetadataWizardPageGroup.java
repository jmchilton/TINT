package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.Collection;

import edu.umn.msi.tropix.webgui.client.components.newwizards.MetadataWizardPageFactory.MetadataWizardPageImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageGroup;

public class MetadataWizardPageGroup extends WizardPageGroup<MetadataWizardPageImpl> {
  private final MetadataWizardPageImpl singleObjectWizardPage;
  private final MetadataWizardPageImpl batchWizardPage;

  public MetadataWizardPageImpl getSingleObjectWizardPage() {
    return singleObjectWizardPage;
  }

  protected MetadataWizardPageGroup(final MetadataWizardPageFactory factory,
      final Collection<TreeItem> locations,
      final String singleObjectType,
      final String batchObjectType) {
    super(Lists.newArrayList(factory.get(locations, singleObjectType),
        factory.get(locations, batchObjectType)));
    singleObjectWizardPage = super.getWizardPage(0);
    batchWizardPage = super.getWizardPage(1);
  }

  public boolean isBatch() {
    return super.getEnabledWizardPage() == batchWizardPage;
  }

  public void setBatch(final boolean batch) {
    super.enableOnly(batch ? batchWizardPage : singleObjectWizardPage);
  }

}
