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

package edu.umn.msi.tropix.common.jobqueue.execution.system;

import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.execution.system.jpa.PersistentQueueService;

public class PersistentQueueServiceNotTransactionalTest {

  @Test(groups = "oneoff")
  public void put() {
    final FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("classpath:edu/umn/msi/tropix/common/jobqueue/execution/system/databaseContext.xml");
    final PersistentQueueService queueService = (PersistentQueueService) context.getBean("persistentQueueServiceImpl");
    final Queue queue = queueService.load(4L);
    queueService.pushJob(queue, "1234", "description");
    queueService.pushJob(queue, "12345", "description");
    queueService.pushJob(queue, "12346", "description");
    queueService.pushJob(queue, "12347", "description");
    queueService.removeJob(queue, "12347");
    queueService.pushJob(queue, "12348", "description");
    queueService.popJob(queue);
  }

}
