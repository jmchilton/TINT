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

package edu.umn.msi.tropix.common.jobqueue.service;

import net.jmchilton.concurrent.CountDownLatch;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.concurrent.Executors;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;

public class ThreadLocalTicketBeanTest {

  @Test(groups = "unit")
  public void testThreadedBean() {
    final ThreadLocalTicketBean ticketBean = new ThreadLocalTicketBean();
    final CountDownLatch latch = new CountDownLatch(1);
    // Set ticket to moo in this thread
    ticketBean.set(new Ticket("moo"));
    
    Executors.getNewThreadExecutor().execute(new Runnable() {
      public void run() {
        // Set to cow in this thread
        ticketBean.set(new Ticket("cow"));
        
        // Verify ticket is cow in this thread
        assert ticketBean.get().equals(new Ticket("cow"));
        latch.countDown();
      }      
    });
    
    latch.await();
    // Verify in this thread ticket is still moo
    assert ticketBean.get().getValue().equals("moo");
    
  }
  
}
