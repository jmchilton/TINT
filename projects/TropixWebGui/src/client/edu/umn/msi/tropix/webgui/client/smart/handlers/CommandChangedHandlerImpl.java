package edu.umn.msi.tropix.webgui.client.smart.handlers;

import com.google.gwt.user.client.Command;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

public class CommandChangedHandlerImpl extends BaseCommandHandler implements ChangedHandler {

  public CommandChangedHandlerImpl(final Command command) {
    super(command);
  }

  public void onChanged(final ChangedEvent event) {
    super.handleEvent();
  }


}
