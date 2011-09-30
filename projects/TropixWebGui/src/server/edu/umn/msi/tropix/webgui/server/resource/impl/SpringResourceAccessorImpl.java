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

package edu.umn.msi.tropix.webgui.server.resource.impl;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.webgui.server.resource.ResourceAccessor;

/**
 * The purpose of this class is to act as a wrapper around Spring's
 * concept of Resources so that the static dependencies on Spring are
 * held to as few packages as possible.
 * 
 * @author John Chilton
 *
 */
class SpringResourceAccessorImpl implements ResourceAccessor, ApplicationContextAware {
  private ApplicationContext applicationContext;
  private String resourceId;

  public InputStream get() {
    Preconditions.checkNotNull(resourceId, "Resource identifier is null.");
    final Resource resource = this.applicationContext.getResource(this.resourceId);
    try {
      return resource.getInputStream();
    } catch(final IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public void setApplicationContext(final ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  public void setResourceId(final String resourceId) {
    this.resourceId = resourceId;
  }

}
