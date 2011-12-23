package edu.umn.msi.tropix.webgui.client.components;

import com.google.gwt.user.client.Command;

public interface DescribableLocationCommandComponentFactory<T extends Command> extends LocationCommandComponentFactory<T> {

  String getDescription();
  
}
