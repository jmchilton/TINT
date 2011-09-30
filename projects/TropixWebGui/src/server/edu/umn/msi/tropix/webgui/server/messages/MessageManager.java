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

package edu.umn.msi.tropix.webgui.server.messages;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.webgui.server.security.UserSession;

@ManagedBean
public class MessageManager<T> implements MessagePusher<T> {
  private static final Log LOG = LogFactory.getLog(MessageManager.class);
  private final Multimap<String, String> userToSessionMap = HashMultimap.create();
  private final Map<String, String> sessionToUserMap = Maps.newHashMap();
  private final Map<String, CircularFifoBuffer> bufferMap = Maps.newHashMap();
  private final UserSession userSession;
  private final Supplier<HttpSession> httpSessionSupplier;

  @Inject
  public MessageManager(final UserSession userSession, 
                        @Named("httpSessionSupplier") final Supplier<HttpSession> httpSessionSupplier) {
    LOG.info("Constructing an instance of MessageManager.");
    this.userSession = userSession;
    this.httpSessionSupplier = httpSessionSupplier;
  }
  
  public Collection<T> drainQueue() {
    final HttpSession session = httpSessionSupplier.get();
    final String sessionId = session.getId();
    final String userId = userSession.getGridId();
    final List<T> objects = Lists.newArrayList();
    if(userId == null) {
      return objects;
    }
    CircularFifoBuffer queue;
    synchronized(userToSessionMap) {
      if(!userToSessionMap.containsEntry(userId, sessionId)) {
        final CloseWithSession finalizer = new CloseWithSession(userId, sessionId);
        session.setAttribute("messageManagerFinalizingObject", finalizer);
        userToSessionMap.put(userId, sessionId);
        sessionToUserMap.put(sessionId, userId);
      }
      if(!bufferMap.containsKey(sessionId)) {
        final CircularFifoBuffer objectBuffer = new CircularFifoBuffer(25);
        bufferMap.put(sessionId, objectBuffer);
      }
      queue = bufferMap.get(sessionId);
    }
    synchronized(queue) {
      while(!queue.isEmpty()) {
        @SuppressWarnings("unchecked")
        final T object = (T) queue.remove();
        if(object == null) {
          break;
        }
        objects.add(object);
      }
    }
    return objects;
  }

  // Returns a copy (for thread safety) of the session ids corresponding to the
  // given user ids
  private Collection<String> getSessionIds(final String userId) {
    synchronized(userToSessionMap) {
      return Lists.newArrayList(userToSessionMap.get(userId));
    }
  }

  public void push(final String userId, final T message) {
    this.doPush(userId, message);
  }

  private void doPush(final String userId, final T message) {
    final Collection<String> sessionIds = this.getSessionIds(userId);
    for(final String sessionId : sessionIds) {
      final CircularFifoBuffer buffer = this.bufferMap.get(sessionId);
      if(buffer != null) {
        synchronized(buffer) {
          buffer.add(message);
        }
      }
    }
  }

  public void push(final T object) {
    this.push(userSession.getGridId(), object);
  }

  @VisibleForTesting
  class CloseWithSession {
    private final String userId;
    private final String sessionId;
    private final boolean doFinalize = true;

    CloseWithSession(final String userId, final String sessionId) {
      this.userId = userId;
      this.sessionId = sessionId;
    }

    @VisibleForTesting
    public void performFinalization() {
      LOG.debug("finalize called for userId " + this.userId + " and sessionId " + this.sessionId);
      if(doFinalize) {
        synchronized(userToSessionMap) {
          userToSessionMap.remove(this.userId, this.sessionId);
          sessionToUserMap.remove(this.sessionId);
          bufferMap.remove(this.sessionId);
        }
      }      
    }
    
    public void finalize() {
      performFinalization();
    }
  }

}
