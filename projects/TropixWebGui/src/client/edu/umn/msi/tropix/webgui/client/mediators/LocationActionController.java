package edu.umn.msi.tropix.webgui.client.mediators;

import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.umn.msi.tropix.webgui.client.components.DescribableLocationCommandComponentFactory;

public class LocationActionController {
  private LocationActionDescriptionManager locationActionDescriptionManager;
  
  @Inject
  public LocationActionController(final LocationActionDescriptionManager locationActionDescriptionManager) {
    this.locationActionDescriptionManager = locationActionDescriptionManager;
  }

  @Inject
  public void setRenameFactory(@Named("rename") final DescribableLocationCommandComponentFactory<? extends Command> commandFactory) {
    locationActionDescriptionManager.register(commandFactory);
  }

  @Inject
  public void setChangeDescriptionFactory(@Named("changeDescription") final DescribableLocationCommandComponentFactory<? extends Command> commandFactory) {
    locationActionDescriptionManager.register(commandFactory);
  }

  @Inject
  public void setMoveFactory(@Named("move") final DescribableLocationCommandComponentFactory<? extends Command> commandFactory) {
    locationActionDescriptionManager.register(commandFactory);
  }

  @Inject
  public void setDeleteFactory(@Named("delete") final DescribableLocationCommandComponentFactory<? extends Command> commandFactory) {
    locationActionDescriptionManager.register(commandFactory);
  }

}
