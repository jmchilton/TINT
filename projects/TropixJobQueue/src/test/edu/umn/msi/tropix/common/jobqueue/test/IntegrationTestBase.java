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

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

import net.jmchilton.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.jobqueue.QueueStage;
import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.client.JobPoller;
import edu.umn.msi.tropix.common.jobqueue.client.JobUpdateListener;
import edu.umn.msi.tropix.common.jobqueue.client.impl.DelegatingJobUpdateListenerImpl;
import edu.umn.msi.tropix.common.jobqueue.service.FileJobQueueContext;
import edu.umn.msi.tropix.common.jobqueue.service.JobQueueContext;
import edu.umn.msi.tropix.common.jobqueue.status.StageEnumeration;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.test.FreshConfigTest;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.storage.client.StorageDataFactory;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@ContextConfiguration(locations = "integrationTestContext.xml")
public class IntegrationTestBase extends FreshConfigTest {
  private static final Log LOG = LogFactory.getLog(IntegrationTestBase.class);
  
  @Autowired
  private StorageDataFactory storagedDataFactory;

  @Resource
  private JobPoller jobPoller;
  
  @Autowired
  private DelegatingJobUpdateListenerImpl dListener;

  private String storageServiceUrl = null;
  
  private List<StorageData> results = Lists.newArrayList();
  
  private boolean jobCompletedProperly;
  
  protected List<StorageData> getResults() {
    return results;
  }
  
  
  @BeforeMethod(groups = "spring")
  public void initJobIntegration() {
    results.clear();
    jobCompletedProperly = false;
  }
  
  
  
  protected TransferResource getNewReference() {
    final StorageData storageData = storagedDataFactory.getStorageData(storageServiceUrl, null);
    return storageData.prepareUploadResource();
  }

  protected StorageData getStorageData() {
    return storagedDataFactory.getStorageData(storageServiceUrl, null);
  }

  protected StorageData getStorageData(final String id) {
    return storagedDataFactory.getStorageData(id, storageServiceUrl, null);
  }

  protected List<String> getStorageIds(final List<InputStream> streams) {
    final List<String> ids = new LinkedList<String>();
    try {
      for (final InputStream stream : streams) {
        final StorageData storageData = storagedDataFactory.getStorageData(storageServiceUrl, null);
        storageData.getUploadContext().put(stream);
        final String id = storageData.getDataIdentifier();
        ids.add(id);
      }
    } catch (final Exception e) {
      throw new IllegalStateException("Failed to prepare ids for download", e);
    }
    return ids;
  }

  protected List<TransferResource> getReferencesForIds(final List<String> ids) {
    final List<TransferResource> refs = new LinkedList<TransferResource>();
    for (final String id : ids) {
      final StorageData storageData = storagedDataFactory.getStorageData(id, storageServiceUrl,  null);
      // System.out.println("Storage data " + storageData + " id is " + id);
      refs.add(storageData.prepareDownloadResource());
    }
    return refs;
  }

  protected TransferResource getReference(final InputStream stream) {
    return getReferences(Arrays.asList(stream)).get(0);
  }

  protected List<TransferResource> getReferences(final List<InputStream> streams) {
    return getReferencesForIds(getStorageIds(streams));
  }
  
  protected boolean pollJob(final String jobType, final JobQueueContext context) {
    final Job job = new Job();
    job.setJobType(jobType);  
    job.setTicket(context.getTicket());
    return pollJob(job, context);
  }
  
  protected boolean pollJob(final Job job, final JobQueueContext context) {
    final CountDownLatch completeLatch = new CountDownLatch(1), postprocessLatch = new CountDownLatch(1);
    final AtomicBoolean previouslyLaunchedFileTransfer = new AtomicBoolean(false);
    class Listener implements JobUpdateListener {
      private boolean finishedProperly = false;

      public void jobComplete(final Ticket ticket, final boolean finishedProperly, final Status finalStatus) {
        this.finishedProperly = finishedProperly;
        completeLatch.countDown();
      }

      public void update(final Ticket ticket, final Status status) {
        final QueueStage stage = QueueStage.fromStatusUpdateList(status);
        LOG.info("Queue stage is [" + stage.getStageEnumerationValue().getValue() + "]");
        if(stage.getStageEnumerationValue() == StageEnumeration.Postprocessed
            && startTransfer()) {
          postprocessLatch.countDown();
        }
      }
      
      private boolean startTransfer() {
        final boolean wasFalse = previouslyLaunchedFileTransfer.getAndSet(true);
        return !wasFalse;
      }
      
    }
    final Listener listener = new Listener();
    dListener.setJobUpdateListener(listener);
    jobPoller.pollJob(job);
    
    postprocessLatch.await();
    final FileJobQueueContext fileContext = (FileJobQueueContext) context;
    final int numResults = fileContext.getNumResults();
    final TransferResource[] results = new TransferResource[numResults];
    for(int i = 0; i < numResults; i++) {
      final StorageData storageData = getStorageData();
      getResults().add(storageData);
      results[i] = storageData.prepareUploadResource();
    }          
    fileContext.getResults(results, null);    
    completeLatch.await();
    jobCompletedProperly = listener.finishedProperly;
    return jobCompletedProperly;
  }
  
  protected void assertJobFinishedProperly() {
    assert jobCompletedProperly;
  }
  
  
}
