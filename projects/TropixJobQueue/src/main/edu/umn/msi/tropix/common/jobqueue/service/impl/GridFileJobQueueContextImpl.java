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

package edu.umn.msi.tropix.common.jobqueue.service.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import edu.umn.msi.tropix.common.concurrent.Timer;
import edu.umn.msi.tropix.common.io.DisposableResource;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.jobqueue.FileJobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobDescription;
import edu.umn.msi.tropix.common.jobqueue.JobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorPostProcessedListener;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfigurationFactories;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfigurationFactory;
import edu.umn.msi.tropix.common.jobqueue.service.FileJobQueueContext;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.common.logging.LogExceptions;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.CredentialResourceResolver;
import edu.umn.msi.tropix.grid.io.transfer.TransferResourceContextFactory;
import edu.umn.msi.tropix.transfer.types.TransferResource;

// TODO: Use MapMaker for timeout...
public class GridFileJobQueueContextImpl<T extends JobDescription> extends JobProcessorQueueContextImpl<T> implements FileJobQueueContext, JobProcessorPostProcessedListener<T> {
  private static final Log LOG = LogFactory.getLog(GridFileJobQueueContextImpl.class);
  private static final long DEFAULT_TIMEOUT = 1000 * 60 * 30;
  private CredentialResourceResolver credentialResourceResolver;
  private TransferResourceContextFactory transferContextsFactory;
  private Executor executor;
  private final JobProcessorConfigurationFactory jobProcessorConfigurationFactory = JobProcessorConfigurationFactories.getInstance();
  private final ConcurrentHashMap<Ticket, List<DisposableResource>> resultMap = new ConcurrentHashMap<Ticket, List<DisposableResource>>();
  private Timer timer;
  private long timeout = DEFAULT_TIMEOUT;

  protected JobProcessorConfiguration getConfiguration(final Credential credential) {
    return jobProcessorConfigurationFactory.get(credential);
  }

  @LogExceptions
  public int getNumResults() {
    final List<DisposableResource> resources = getResources();
    return resources.size();
  }

  private List<DisposableResource> getResources(final Ticket ticket) {
    final List<DisposableResource> resources = this.resultMap.get(ticket);
    return resources;
  }

  private List<DisposableResource> getResources() {
    //System.out.println("Getting resout for ticket[" + getTicket() + "]");
    return getResources(getTicket());
  }
  
  protected Credential getProxy(@Nullable final CredentialResource credentialResource) {
    return credentialResource == null ? null : credentialResourceResolver.getCredential(credentialResource);
  }

  private void handleResults(final Ticket ticket, final TransferResource[] transferRequests, final CredentialResource credentialResource) {
    try {
      transferring(ticket);
      final List<DisposableResource> resources = getResources(ticket);
      if(resources.size() != transferRequests.length) {
        LOG.warn("getResults called with an incorrect number of files");
        throw new IllegalArgumentException("Incorrect number of transfer requests made.");
      }
      final Credential proxy = getProxy(credentialResource);
      try {
        LOG.debug("Preparing to upload " + transferRequests.length + " files");
        for(int i = 0; i < transferRequests.length; i++) {
          LOG.debug("Uploading file number " + i);
          final OutputContext outputContext = transferContextsFactory.getUploadContext(transferRequests[i], proxy);
          final DisposableResource resource = resources.get(i);
          outputContext.put(resource.getFile());
          resource.dispose();
          LOG.debug("Deleted file number " + i + " locally.");
        }
      } catch(final Throwable t) {
        throw ExceptionUtils.logAndConvert(LOG, t, "Failed to upload a requested file", IllegalStateException.class);
      }
    } catch(final RuntimeException e) {
      fail(ticket);
      throw e;
    }
    LOG.debug("getResults returning, files successfully uploaded");
    complete(ticket);
  }
  
  public void getResults(final TransferResource[] transferRequests, final CredentialResource credentialResource) {
    final Ticket ticket = getTicket();
    executor.execute(new Runnable() {
      public void run() {
        handleResults(ticket, transferRequests, credentialResource);
      }
    });
  }

  public void getResults(final TransferResource transferRequest, final CredentialResource credentialResource) {
    getResults(new TransferResource[] {transferRequest}, credentialResource);
  }

  public <S extends T> void jobPostProcessed(final Ticket ticket, final String jobType, final JobProcessor<S> jobProcessor, final boolean completedNormally) {
    LOG.debug("jobComplete signaled for ticket  " + ticket.getValue() + " completedNormally(" + completedNormally + ") ");
    if(completedNormally && jobProcessor instanceof FileJobProcessor<?>) {
      final FileJobProcessor<S> fileJobProcessor = (FileJobProcessor<S>) jobProcessor;
      final List<DisposableResource> results = fileJobProcessor.getResults();
      LOG.debug("Placing " + results.size() + " resluts in in resultMap for ticket " + ticket.getValue());
      resultMap.put(ticket, results);
      timer.schedule(new Runnable() {
        public void run() {
          clean(ticket);
        }
      }, timeout);
    }
  }

  private void clean(final Ticket ticket) {
    if(resultMap.containsKey(ticket)) {
      final List<DisposableResource> resources = resultMap.remove(ticket);
      for(final DisposableResource resource : resources) {
        try {
          resource.dispose();
        } catch(final Throwable t) {
          ExceptionUtils.logQuietly(LOG, t, "Failed to dispose of resource " + resource);
        }
      }
    }
  }

  protected InputContext getDownloadContext(final TransferResource tscReference, final Credential proxy) {
    return transferContextsFactory.getDownloadContext(tscReference, proxy);
  }

  protected ImmutableList<InputContext> getDownloadContexts(final TransferResource[] tscReferences, final Credential proxy) {
    final Builder<InputContext> contexts = ImmutableList.builder();
    for(final TransferResource tscReference : tscReferences) {
      contexts.add(getDownloadContext(tscReference, proxy));
    }
    return contexts.build();
  }

  public void setTimer(final Timer timer) {
    this.timer = timer;
  }

  public void setTimeout(final long timeout) {
    this.timeout = timeout;
  }

  public void setTransferContextsFactory(final TransferResourceContextFactory transferContextsFactory) {
    this.transferContextsFactory = transferContextsFactory;
  }

  public void setCredentialResourceResolver(final CredentialResourceResolver credentialResourceResolver) {
    this.credentialResourceResolver = credentialResourceResolver;
  }
  
  public void setExecutor(final Executor executor) {
    this.executor = executor;
  }

}