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

package edu.umn.msi.tropix.client.metadata.impl;

import java.util.Map;

import javax.xml.namespace.QName;

import edu.umn.msi.tropix.client.metadata.MetadataResolver;

public class DelegatedMetadataResolverImpl implements MetadataResolver {
  private Map<String, MetadataResolver> metadataResolvers;
  
  public void setMetadataResolvers(final Map<String, MetadataResolver> metadataResolvers) {
    this.metadataResolvers = metadataResolvers;
  }

  public <T> T getMetadata(final String address, final QName qName, final Class<T> metadataClass) {
    MetadataResolver resolver = null;
    for(Map.Entry<String, MetadataResolver> entry : metadataResolvers.entrySet()) {
      final String prefix = entry.getKey();
      if(address.startsWith(prefix)) {
        resolver = entry.getValue();
        break;
      }
    }
    return resolver.getMetadata(address, qName, metadataClass);
  }

}
