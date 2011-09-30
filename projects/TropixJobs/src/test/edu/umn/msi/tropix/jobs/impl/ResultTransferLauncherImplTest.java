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

package edu.umn.msi.tropix.jobs.impl;

import java.util.Arrays;
import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.jobqueue.service.FileJobQueueContext;
import edu.umn.msi.tropix.common.jobqueue.status.Stage;
import edu.umn.msi.tropix.common.jobqueue.status.StageEnumeration;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.jobs.activities.descriptions.PollJobDescription;
import edu.umn.msi.tropix.jobs.services.JobContextClientFactory;

public class ResultTransferLauncherImplTest {

  private TransferClient transferClient;
  private JobContextClientFactory jobContextClientFactory;
  private ResultTransferLauncherImpl launcher;
  private PollJobDescription description;
  private Credential credential;
  private String url, ticket, storageServiceUrl;
  private FileJobQueueContext context;
  private String[] ids;

  @BeforeMethod(groups = "unit")
  public void init() {
    transferClient = EasyMock.createMock(TransferClient.class);
    jobContextClientFactory = EasyMock.createMock(JobContextClientFactory.class);
    context = EasyMock.createMock(FileJobQueueContext.class);
    description = new PollJobDescription();
    credential = Credentials.getMock();
    url = "http://" + UUID.randomUUID().toString();
    storageServiceUrl = "http://" + UUID.randomUUID().toString();
    ticket = UUID.randomUUID().toString();
    description.setServiceUrl(url);
    description.setTicket(ticket);
    description.setStorageServiceUrl(storageServiceUrl);
    ids = randomIds();

    launcher = new ResultTransferLauncherImpl(transferClient, jobContextClientFactory);
  }

  private void update() {
    final Status status = new Status();
    final Stage stage = new Stage();
    stage.setValue(StageEnumeration.Postprocessed);
    status.setStage(stage);
    launcher.update(new Ticket(ticket), status);
  }

  private String[] randomIds() {
    final String[] ids = new String[] {UUID.randomUUID().toString(), UUID.randomUUID().toString()};
    return ids;
  }

  private void replay() {
    EasyMock.replay(transferClient, jobContextClientFactory, context);
  }

  private void verify() {
    EasyMockUtils.verifyAndReset(transferClient, jobContextClientFactory, context);
  }

  private void expectTransfer() {
    EasyMock.expect(jobContextClientFactory.getJobClient(credential, url, ticket, FileJobQueueContext.class))
        .andReturn(context);
    EasyMock.expect(transferClient.transferResults(EasyMock.<Integer>isNull(),
                    EasyMock.eq(context),
                    EasyMock.eq(storageServiceUrl),
                    EasyMock.eq(credential))).andReturn(ids);
  }

  private void checkIds() {
    assert Iterables.elementsEqual(description.getFileIds().toList(), Arrays.asList(ids));
  }

  @Test(groups = "unit", timeOut = 1000)
  public void registerFirst() {
    replay();
    launcher.register(credential, description);
    verify();
    expectTransfer();
    replay();
    update();
    verify();
    checkIds();
  }

  @Test(groups = "unit", timeOut = 1000)
  public void registerSecond() {
    replay();
    update();
    verify();
    expectTransfer();
    replay();
    launcher.register(credential, description);
    verify();
    checkIds();
  }

}
