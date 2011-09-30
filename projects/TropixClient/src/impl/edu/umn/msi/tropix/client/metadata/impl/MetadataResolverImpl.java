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

import java.io.StringReader;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.globus.wsrf.utils.XmlUtils;
import org.w3c.dom.Element;

import com.google.common.annotations.VisibleForTesting;

import edu.umn.msi.tropix.client.metadata.MetadataResolver;
import edu.umn.msi.tropix.grid.EprUtils;
import gov.nih.nci.cagrid.common.Utils;

public class MetadataResolverImpl implements MetadataResolver {
  private MetadataElementResolver metadataElementResolver = MetadataElementResolvers.getInstance();

  private <T> T getMetadata(final EndpointReferenceType epr, final QName qName, final Class<T> metadataClass) {
    final Element metadataElement = metadataElementResolver.getMetadataElement(epr, qName);
    final String metadataStr = XmlUtils.toString(metadataElement);
    try {
      @SuppressWarnings("unchecked")
      final T metadata = (T) Utils.deserializeObject(new StringReader(metadataStr), metadataClass);
      return metadata;
    } catch(final Exception e) {
      throw new RuntimeException("Deserialization failed.", e);
    }
  }

  public <T> T getMetadata(final String address, final QName name, final Class<T> metadataClass) {
    final EndpointReferenceType epr = EprUtils.getEprForAddress(address);
    return getMetadata(epr, name, metadataClass);
  }

  @VisibleForTesting
  void setMetadataElementResolver(final MetadataElementResolver metadataElementResolver) {
    this.metadataElementResolver = metadataElementResolver;
  }

}
