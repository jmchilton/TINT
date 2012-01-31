package edu.umn.msi.tropix.webgui.client.components.newwizards;

import edu.umn.msi.tropix.client.services.GridService;

public interface ServiceWizardPage<T extends GridService> {

  T getGridService();

}