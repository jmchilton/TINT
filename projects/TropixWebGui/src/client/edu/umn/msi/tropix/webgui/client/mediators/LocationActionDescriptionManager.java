package edu.umn.msi.tropix.webgui.client.mediators;

import java.util.Collection;

import com.google.gwt.user.client.Command;

import edu.umn.msi.tropix.webgui.client.components.DescribableLocationCommandComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;

public interface LocationActionDescriptionManager {

  void register(final DescribableLocationCommandComponentFactory<? extends Command> description);
  
  Collection<DescribableLocationCommandComponentFactory<? extends Command>> getMatchingLocationActionDescriptions(final Collection<TreeItem> treeItems);
  
}
