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
import javax.inject.Named;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.TissueSample;
import edu.umn.msi.tropix.persistence.service.SampleService;
import edu.umn.msi.tropix.persistence.service.impl.utils.Predicates;

@ManagedBean @Named("sampleService")
class SampleServiceImpl extends ServiceBase implements SampleService {

  public TissueSample createTissueSample(final String userGridId, final String folderId, final TissueSample sample) {
    saveNewObjectToDestination(sample, userGridId, folderId);
    return sample;
  }

  public ProteomicsRun[] getProteomicsRuns(final String userId, final String tissueSampleId) {
    final TissueSample sample = getTropixObjectDao().loadTropixObject(tissueSampleId, TissueSample.class);
    Iterable<ProteomicsRun> runs = sample.getProteomicsRuns();
    if(runs == null) {
      runs = Lists.newArrayList();
    }
    return Iterables.toArray(Iterables.filter(runs, Predicates.getValidAndCanReadPredicate(getSecurityProvider(), userId)), ProteomicsRun.class);
  }
}
