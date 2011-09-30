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

package edu.umn.msi.tropix.webgui.server;

import java.util.Collection;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.collect.Collections;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.persistence.service.AnalysisService;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizer;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizerUtils;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.services.protip.IdentificationJob;

@ManagedBean
public class IdentificationJobImpl implements IdentificationJob {
  private final AnalysisService analysisService;
  private final BeanSanitizer beanSanitizer;
  private final UserSession userSession;
  
  @Inject
  IdentificationJobImpl(final AnalysisService analysisService, final BeanSanitizer beanSanitizer, final UserSession userSession) {
    this.analysisService = analysisService;
    this.beanSanitizer = beanSanitizer;
    this.userSession = userSession;
  }

  @ServiceMethod(readOnly = true)
  public Collection<ProteomicsRun> getRuns(final Collection<String> sourceIds) {
    final ProteomicsRun[] runs = analysisService.getRuns(userSession.getGridId(), Iterables.toArray(sourceIds, String.class));
    return Sets.newHashSet(Collections.transform(Lists.newArrayList(runs), BeanSanitizerUtils.<ProteomicsRun>asFunction(beanSanitizer)));
  }

}