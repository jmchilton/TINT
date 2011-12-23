package edu.umn.msi.tropix.webgui.client.mediators;

import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;

import edu.umn.msi.tropix.webgui.client.mediators.ActionMediator.ActionEvent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;

/**
 * Listener that responds to an ActionEvent by creating a command via given {@link Supplier}
 * and executing that command. 
 * 
 * @author John Chilton (jmchilton at gmail dot com)
 *
 */
class CommandActionEventListener implements Listener<ActionEvent> {
  private Supplier<? extends Command> commandSupplier;
  
  CommandActionEventListener(final Supplier<? extends Command> commandSupplier) {
    this.commandSupplier = commandSupplier;
  }
  
  public void onEvent(final ActionEvent event) {
    this.commandSupplier.get().execute();
  }
  
}