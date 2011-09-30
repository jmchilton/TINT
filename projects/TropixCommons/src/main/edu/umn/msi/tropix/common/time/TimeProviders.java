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

package edu.umn.msi.tropix.common.time;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Utility class for the {@link TimeProvider} interface. 
 * 
 * @author John Chilton
 *
 */
public class TimeProviders {
  /**
   * @return A default implementation of the {@link TimeProvider} interface that simply
   * wraps System.currentTimeMillis();
   */
  public static TimeProvider getInstance() {
    return DEFAULT_TIME_PROVIDER;
  }
  
  private static class DefaultTimeProviderImpl implements TimeProvider, Serializable {

    public long currentTimeMillis() {
      return System.currentTimeMillis();
    }
    
  }
  
  private static final TimeProvider DEFAULT_TIME_PROVIDER = new DefaultTimeProviderImpl();
  
  /**
   * @param times An Iterable describing the times that should be returned by the 
   * resulting TimeProvider instance.
   * 
   * @return A TimeProvider implementation that just returns the supplied times in the
   * order they are supplied. This implementation should only be used for testing.
   */
  public static TimeProvider getFixedTimeProvider(final Iterable<Long> times) {
    return new TimeProvider() {
      private final Iterator<Long> timesIterator = times.iterator();
      
      public long currentTimeMillis() {
        return timesIterator.next();
      }
    };    
  }
  
}
