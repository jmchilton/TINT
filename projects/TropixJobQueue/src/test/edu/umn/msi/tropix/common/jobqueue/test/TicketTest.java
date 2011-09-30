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

package edu.umn.msi.tropix.common.jobqueue.test;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.impl.TicketSupplierImpl;
import edu.umn.msi.tropix.common.jobqueue.service.TicketBean;
import edu.umn.msi.tropix.common.jobqueue.service.TicketBeanTicketProvider;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;

public class TicketTest {

  @Test(groups = "unit")
  public void ticketSupplier() {
    final TicketSupplierImpl supplier = new TicketSupplierImpl();
    assert supplier.get().getValue() != supplier.get().getValue();
  }

  @Test(groups = "unit")
  public void ticketBean() {
    final TicketBean ticketBean = new TicketBean();
    assert ticketBean.get() == null;
    final Ticket ticket = new Ticket("123");
    ticketBean.set(ticket);
    assert ticket.equals(ticketBean.get());
  }

  @Test(groups = "unit")
  public void ticketBeanTicketProvider() {
    final TicketBeanTicketProvider provider = new TicketBeanTicketProvider();
    final TicketBean ticketBean = new TicketBean();
    provider.setTicketBean(ticketBean);
    assert provider.get() == null;
    final Ticket ticket = new Ticket("12345");
    provider.set(ticket);
    assert ticket.equals(ticketBean.get());
    assert ticket.equals(provider.get());
  }

}
