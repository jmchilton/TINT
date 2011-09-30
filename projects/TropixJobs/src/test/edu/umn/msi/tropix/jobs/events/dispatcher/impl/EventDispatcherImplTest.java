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

import java.util.Arrays;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.jobs.events.CompletionEvent;
import edu.umn.msi.tropix.jobs.events.EventHandler;
import edu.umn.msi.tropix.jobs.events.impl.CompletionEventImpl;

public class EventDispatcherImplTest {
  private EventDispatcherImpl dispatcher;
  private EventHandler handler;
  private CompletionEvent completionEvent = new CompletionEventImpl();

  @BeforeMethod(groups = "unit")
  public void init() {
    dispatcher = new EventDispatcherImpl();
    handler = EasyMock.createMock(EventHandler.class);
    dispatcher.setEventHandlers(Arrays.asList(handler));

  }

  @Test(groups = "unit", timeOut = 1000)
  public void delegation() {
    handler.handleEvent(EasyMock.same(completionEvent));
    EasyMock.replay(handler);
    dispatcher.fireEvent(completionEvent);
    EasyMock.verify(handler);
  }

  @Test(groups = "unit", timeOut = 1000)
  public void handleException() {
    handler.handleEvent(EasyMock.same(completionEvent));
    EasyMock.expectLastCall().andThrow(new IllegalStateException());
    EasyMock.replay(handler);
    dispatcher.fireEvent(completionEvent);
    EasyMock.verify(handler);
  }
}
