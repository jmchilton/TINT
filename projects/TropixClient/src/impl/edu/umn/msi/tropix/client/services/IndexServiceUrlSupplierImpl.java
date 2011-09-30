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

package edu.umn.msi.tropix.client.services;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import gov.nih.nci.cagrid.discovery.client.DiscoveryClient;

import java.util.Arrays;
import java.util.Iterator;

import javax.annotation.Nullable;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.springframework.util.StringUtils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;

public class IndexServiceUrlSupplierImpl implements Iterable<String> {
  @Nullable
  private String indexServiceUrl;
  private Function<String, DiscoveryClient> discoveryClientFactory = new DiscoveryClientFunctionImpl();

  private static final Function<EndpointReferenceType, String> GET_ADDRESS = new Function<EndpointReferenceType, String>() {
    public String apply(final EndpointReferenceType epr) {
      return epr.getAddress().toString();
    }
  };

  public Iterator<String> iterator() {
    if(!StringUtils.hasText(indexServiceUrl)) {
      return Iterators.emptyIterator();
    }
    final DiscoveryClient client = discoveryClientFactory.apply(indexServiceUrl);
    EndpointReferenceType[] eprs;
    try {
      eprs = client.getAllServices(false);
    } catch(final Exception t) {
      throw ExceptionUtils.convertException(t);
    }
    return Iterators.transform(Arrays.asList(eprs).iterator(), GET_ADDRESS);
  }

  public void setIndexServiceUrl(@Nullable final String indexServiceUrl) {
    this.indexServiceUrl = indexServiceUrl;
  }

  @VisibleForTesting
  void setDiscoveryClientFunction(final Function<String, DiscoveryClient> discoveryClientFactory) {
    this.discoveryClientFactory = discoveryClientFactory;
  }

}
