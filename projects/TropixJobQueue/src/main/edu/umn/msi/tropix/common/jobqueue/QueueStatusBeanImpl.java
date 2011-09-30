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

package edu.umn.msi.tropix.common.jobqueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.jobqueue.queuestatus.QueueStatus;

// For some reason Spring doesn't proxy correctly if this implements
// a QueueStatusBean interface.
public class QueueStatusBeanImpl { 
  private static final Log LOG = LogFactory.getLog(QueueStatusBeanImpl.class);
  private QueueStatus queueStatus;

  public QueueStatus get() {
    return queueStatus;
  }

  public void set(final QueueStatus queueStatus) {
    LOG.debug("set of queueStatusBean called");
    this.queueStatus = queueStatus;
  }
  
  public Supplier<QueueStatus> asSupplier() {
    return new Supplier<QueueStatus>() {
      public QueueStatus get() {
        return queueStatus;
      }      
    };
  }
  
}
