/*******************************************************************************
 * Copyright 2009 Regents of the University of Minnesota. All rights
 * reserved.
 * Copyright 2009 Mayo Foundation for Medical Education and Research.
 * All rights reserved.
 *
 * This program is made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS
 * OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A
 * PARTICULAR PURPOSE.  See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 ******************************************************************************/

package edu.umn.msi.tropix.client.request.actions.impl;

import info.minnesotapartnership.tropix.request.models.RequestStatus;
import info.minnesotapartnership.tropix.request.models.ServiceResult;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;

import edu.umn.msi.tropix.common.concurrent.GrouppedExecutor;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

public class PersistentActionManagerImpl implements PersistentActionManager {
  private static final Log LOG = LogFactory.getLog(PersistentActionManagerImpl.class);
  private GrouppedExecutor<String> grouppedExecutor;
  private PersistentActionService persistentActionService;
  private Function<RequestAction, Runnable> requestActionToRunnableFunction;

  private AtomicLong actionCounter;
  private final CountDownLatch countDownLatch = new CountDownLatch(1); // TODO: Replace with runtime interrupts version...

  @PostConstruct
  public void init() {
    final Collection<RequestAction> actions = persistentActionService.loadActions();
    long largestId = 0L;
    for(final RequestAction action : actions) {
      final long actionId = action.getActionId();
      if(actionId > largestId) {
        largestId = actionId;
      }
      execute(action);
    }
    actionCounter = new AtomicLong(largestId + 1);
    countDownLatch.countDown();
  }

  private void await() {
    try {
      countDownLatch.await();
    } catch(final InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void execute(final RequestAction action) {
    final Runnable runnable = requestActionToRunnableFunction.apply(action);
    final String group = action.getRequestId() + "@" + action.getRequestService();
    LOG.info("Executing action " + action + " with group " + group);
    grouppedExecutor.execute(group, runnable);
  }

  public void update(final String requestService, final String requestId, final RequestStatus requestStatus) {
    await();
    final UpdateRequestAction action = new UpdateRequestAction(actionCounter.getAndIncrement(), requestService, requestId, requestStatus.getValue());
    try {
      persistentActionService.persistRequestAction(action);
    } catch(final RuntimeException e) {
      ExceptionUtils.logQuietly(LOG, e, "Failed to persist update action, might never be sent");
    }
    execute(action);
  }

  public void upload(final String requestService, final String requestId, final String fileId, final String storageServiceUrl, final ServiceResult serviceResult) {
    await();
    final UploadRequestAction action = new UploadRequestAction(actionCounter.getAndIncrement(), requestService, requestId, fileId, storageServiceUrl, ServiceResultXmlUtils.toXml(serviceResult));
    try {
      persistentActionService.persistRequestAction(action);
    } catch(final RuntimeException e) {
      ExceptionUtils.logQuietly(LOG, e, "Failed to persist upload action, file may never be sent");
    }
    execute(action);
  }

  public void setGrouppedExecutor(final GrouppedExecutor<String> grouppedExecutor) {
    this.grouppedExecutor = grouppedExecutor;
  }

  public void setPersistentActionService(final PersistentActionService persistentActionService) {
    this.persistentActionService = persistentActionService;
  }

  public void setRequestActionToRunnableFunction(final Function<RequestAction, Runnable> requestActionToRunnableFunction) {
    this.requestActionToRunnableFunction = requestActionToRunnableFunction;
  }

}
