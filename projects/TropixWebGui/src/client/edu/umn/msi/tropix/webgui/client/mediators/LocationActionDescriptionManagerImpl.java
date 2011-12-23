package edu.umn.msi.tropix.webgui.client.mediators;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.Command;
import com.google.inject.Inject;

import edu.umn.msi.tropix.webgui.client.components.DescribableLocationCommandComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.mediators.ActionMediator.ActionEvent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;

public class LocationActionDescriptionManagerImpl implements LocationActionDescriptionManager {
  private ActionMediator actionMediator;
  private Collection<DescribableLocationCommandComponentFactory<? extends Command>> descriptions = new ArrayList<DescribableLocationCommandComponentFactory<? extends Command>>(); 
  
  @Inject
  public LocationActionDescriptionManagerImpl(final ActionMediator actionMediator) {
    this.actionMediator = actionMediator;
  }
  
  public void register(final DescribableLocationCommandComponentFactory<? extends Command> commandFactory) {
    actionMediator.registerActionListener("locationAction" + commandFactory.getDescription(), new Listener<ActionEvent>() {

      public void onEvent(final ActionEvent event) {
        if(event instanceof LocationActionEventImpl) {
          final LocationActionEventImpl locationEvent = (LocationActionEventImpl) event;
          commandFactory.get(locationEvent.getItems()).execute();
        }
      }
      
    });
    
    descriptions.add(commandFactory);
  }
  
  public void fireEventForLocations(final Collection<TreeItem> treeItems, final DescribableLocationCommandComponentFactory<? extends Command> description) {
    final LocationActionEventImpl event = LocationActionEventImpl.forItems("locationAction" + description.getDescription(), treeItems);
    actionMediator.handleEvent(event);
  }

  public Collection<DescribableLocationCommandComponentFactory<? extends Command>> getMatchingLocationActionDescriptions(final Collection<TreeItem> treeItems) {
    final Collection<DescribableLocationCommandComponentFactory<? extends Command>> matchingDescriptions = new ArrayList<DescribableLocationCommandComponentFactory<? extends Command>>();
    for(final DescribableLocationCommandComponentFactory<? extends Command> description : descriptions) {
      if(description.acceptsLocations(treeItems)) {
        matchingDescriptions.add(description);
      }
    }
    return matchingDescriptions;
  }

}
