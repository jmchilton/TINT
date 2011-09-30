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

package edu.umn.msi.tropix.common.message.impl;

import java.util.Locale;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import edu.umn.msi.tropix.common.message.MessageSource;

/**
 * The default spring-based implementation of {@link MessageSource}.
 * 
 * @author John Chilton
 *
 */
@ManagedResource
public class MessageSourceSpringImpl extends ReloadableResourceBundleMessageSource implements MessageSource {
  private Locale locale;

  @ManagedOperation
  @Override
  public void clearCache() {
    super.clearCache();
  }

  public String getMessage(final String code, final Object... args) {
    return super.getMessage(code, args, this.locale);
  }

  public String getMessage(final String code, final Object[] args, final String defaultMessage) {
    return super.getMessage(code, args, defaultMessage, this.locale);
  }

  public void setLocale(final Locale locale) {
    this.locale = locale;
  }
}
