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

package edu.umn.msi.tropix.persistence.service.impl;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.persistence.galaxy.PersistentGalaxyToolStore;
import edu.umn.msi.tropix.persistence.service.GalaxyToolService;

@ManagedBean
class GalaxyToolServiceImpl extends ServiceBase implements GalaxyToolService {
  private PersistentGalaxyToolStore galaxyToolRepository;
  
  @Inject
  GalaxyToolServiceImpl(final PersistentGalaxyToolStore galaxyToolRepository) {
    this.galaxyToolRepository = galaxyToolRepository;
  }
  
  
  public GalaxyTool create(final String userId, final GalaxyTool galaxyTool, final String xml) {
    return galaxyToolRepository.create(galaxyTool, xml);
  }

  public String getXml(final String userId, final String toolId) {
    return galaxyToolRepository.getXml(toolId);
  }

  public GalaxyTool[] list(final String userId) {
    return Iterables.toArray(galaxyToolRepository.list(), GalaxyTool.class);
  }

  public synchronized void update(final String userId, final GalaxyTool galaxyTool, final String updatedXml) {
    galaxyToolRepository.update(galaxyTool, updatedXml);
  }

  public GalaxyTool load(final String userId, final String toolId) {
    return galaxyToolRepository.load(toolId);
  }

}
