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

package edu.umn.msi.tropix.webgui.server.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Map;
import java.util.concurrent.Executor;

import edu.umn.msi.tropix.common.io.IORuntimeException;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.webgui.server.resource.ResourceAccessor;

/**
 * This class is very confused right now in order to quickly optimize the Grid User
 * panel. I need to rewrite this.
 * 
 * @author John Chilton
 *
 */
public class SmartXmlResourceAccessorImpl implements ResourceAccessor {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private Iterable<Map<String, String>> dataSourceIterable;
  private Executor executor;

  public void setDataSourceIterable(final Iterable<Map<String, String>> dataSourceIterable) {
    this.dataSourceIterable = dataSourceIterable;
  }

  public void setExecutor(final Executor executor) {
    this.executor = executor;
  }

  public void setResourceId(final String resourceId) {
    throw new UnsupportedOperationException();
  }

  public InputStream get() {
    final PipedOutputStream outputStream = new PipedOutputStream();
    PipedInputStream inputStream;
    try {
      inputStream = new PipedInputStream(outputStream);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
    this.executor.execute(new Runnable() {
      public void run() {
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        try {
          writer.append("{response:{ status:0, data:[");
          boolean outerFirst = true;
          for(final Map<String, String> properties : dataSourceIterable) {
            if(!outerFirst) {
              writer.append(",");
            } else {
              outerFirst = false;
            }
            writer.append("{");
            boolean first = true;
            for(final Map.Entry<String, String> property : properties.entrySet()) {
              if(!first) {
                 writer.append(",");
              } else {
                first = false;
              }
              final String key = property.getKey();
              writer.append(key + ": \"" + property.getValue() + "\"");
            }
            writer.append("}");
          }
          writer.append("]}}");
          writer.flush();
        } catch(final IOException e) {
          throw new IORuntimeException(e);
        } finally {
          IO_UTILS.closeQuietly(outputStream);          
        }
      }
    });
    return inputStream;
  }
}
