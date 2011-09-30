/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.jobs.events.dispatcher.impl;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.jobs.events.EventBase;
import edu.umn.msi.tropix.jobs.events.EventHandler;
import edu.umn.msi.tropix.jobs.events.dispatcher.EventDispatcher;

// TODO: Consider adding EventHandler config for stuff like launch in
// thread or maybe make this property universal to this object.
public class EventDispatcherImpl implements EventDispatcher {
  private static final Log LOG = LogFactory.getLog(EventDispatcherImpl.class);
  private Collection<EventHandler> eventHandlers = new HashSet<EventHandler>();

  public void setEventHandlers(final Collection<EventHandler> eventHandlers) {
    this.eventHandlers = eventHandlers;
  }

  public void fireEvent(final EventBase event) {
    for(final EventHandler eventHandler : eventHandlers) {
      try {
        eventHandler.handleEvent(event);
      } catch(final Throwable t) {
        ExceptionUtils.logQuietly(LOG, t, "Problem with handleEvent for handler " + eventHandler + " and event " + event);
      }
    }
  }
}
