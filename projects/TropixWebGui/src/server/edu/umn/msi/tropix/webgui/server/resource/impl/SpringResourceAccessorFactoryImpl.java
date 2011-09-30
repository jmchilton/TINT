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

import javax.annotation.ManagedBean;
import javax.inject.Named;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.base.Function;

import edu.umn.msi.tropix.webgui.server.resource.ResourceAccessor;
import edu.umn.msi.tropix.webgui.server.resource.ResourceAccessorFactory;

@ManagedBean @Named("resourceAccessorFactory")
class SpringResourceAccessorFactoryImpl implements ResourceAccessorFactory, ApplicationContextAware, Function<String, ResourceAccessor> {
  private ApplicationContext applicationContext;

  public SpringResourceAccessorImpl get() {
    final SpringResourceAccessorImpl accessor = new SpringResourceAccessorImpl();
    accessor.setApplicationContext(applicationContext);
    return accessor;
  }

  public ResourceAccessor apply(final String resourceId) {
    final SpringResourceAccessorImpl accessor = this.get();
    accessor.setResourceId(resourceId);
    return accessor;
  }

  public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

}
