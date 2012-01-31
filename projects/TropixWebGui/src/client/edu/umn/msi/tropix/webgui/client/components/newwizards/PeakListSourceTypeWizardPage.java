package edu.umn.msi.tropix.webgui.client.components.newwizards;

import edu.umn.msi.tropix.webgui.client.components.newwizards.ProteomicsRunSourceTypeWizardPageImpl.ProteomicsRunSource;
import edu.umn.msi.tropix.webgui.client.utils.Property;

interface PeakListSourceTypeWizardPage {

  Property<ProteomicsRunSource> getProteomicsRunSourceProperty();

  Property<Boolean> getBatchProperty();

}