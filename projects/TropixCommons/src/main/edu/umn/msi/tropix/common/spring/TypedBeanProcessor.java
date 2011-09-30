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

package edu.umn.msi.tropix.common.spring;

import javax.annotation.PostConstruct;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.base.Predicate;

public abstract class TypedBeanProcessor implements ApplicationContextAware {
  private ApplicationContext context;
  private final Predicate<Class<?>> typePredicate;

  protected TypedBeanProcessor(final Class<?> type) {
    this(new Predicate<Class<?>>() {
      public boolean apply(final Class<?> queryClass) {
        return type.isAssignableFrom(queryClass);
      }
    });
  }

  protected TypedBeanProcessor(final Predicate<Class<?>> typePredicate) {
    this.typePredicate = typePredicate;
  }

  protected abstract void processBeans(final Iterable<Object> annotatedBean);

  @PostConstruct
  public void init() {
    processBeans(ApplicationContextUtils.filterBeans(context, typePredicate));
  }

  public void setApplicationContext(final ApplicationContext context) {
    this.context = context;
  }

}
