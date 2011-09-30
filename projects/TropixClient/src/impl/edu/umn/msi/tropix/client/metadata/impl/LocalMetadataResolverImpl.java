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

import javax.xml.namespace.QName;

import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.client.metadata.MetadataResolver;
import edu.umn.msi.tropix.common.spring.TypedBeanProcessor;

public class LocalMetadataResolverImpl extends TypedBeanProcessor implements MetadataResolver {
  private final Multimap<String, LocalMetadataSupplier<?>> addressedMetadataSuppliers = HashMultimap.create();
  
  public LocalMetadataResolverImpl() {
    super(new Predicate<Class<?>>() {
      public boolean apply(final Class<?> classArg) {
        return LocalMetadataSource.class.isAssignableFrom(classArg);
      }      
    });
  } 

  public <T> T getMetadata(final String address, final QName qName, final Class<T> metadataClass) {
    final Iterable<LocalMetadataSupplier<?>> localMetadataSuppliers = addressedMetadataSuppliers.get(address);
    T object = null;
    for(final LocalMetadataSupplier<?> localMetadataSupplier : localMetadataSuppliers) {
      if(localMetadataSupplier.matches(address, qName, metadataClass)) {
        @SuppressWarnings("unchecked")
        T metadata = (T) localMetadataSupplier.get();
        object = metadata;
      }
    }
    return object;
  }

  /**
   * Overrides parent's method which will be called upon initialization with an
   * {@link Iterable} over the beans of type LocalMetadataSource.
   * 
   * @param beans An {@link Iterable} over all of the beans of type LocalMetadataSource.
   */
  @Override
  protected void processBeans(final Iterable<Object> beans) {
    for(Object bean : beans) {
      LocalMetadataSource metadataSource = (LocalMetadataSource) bean;
      addressedMetadataSuppliers.putAll(metadataSource.getLocalAddress(), metadataSource.getLocalMetadataSuppliers());
    }
  }
  
}
