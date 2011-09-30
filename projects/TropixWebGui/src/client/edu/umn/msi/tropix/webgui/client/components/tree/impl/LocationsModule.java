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

package edu.umn.msi.tropix.webgui.client.components.tree.impl;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;

/**
 * Binds various tree and location interfaces defined in
 * edu.umn.msi.tropix.webgui.client.components.tree to concrete
 * classes. 
 * 
 * @author John Chilton
 *
 */
public class LocationsModule  extends AbstractGinModule {

  /**
   * Specifies actual bindings.
   */
  protected void configure() {
    bind(TreeComponentFactory.class).to(TreeComponentFactoryImpl.class).in(Singleton.class);
    bind(LocationFactory.class).to(LocationFactoryImpl.class).in(Singleton.class);
  }
  
}
