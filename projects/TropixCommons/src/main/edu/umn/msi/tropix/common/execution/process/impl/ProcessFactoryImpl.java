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

package edu.umn.msi.tropix.common.execution.process.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.execution.process.Process;
import edu.umn.msi.tropix.common.execution.process.ProcessConfiguration;
import edu.umn.msi.tropix.common.execution.process.ProcessFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;

/**
 * Default instance of the {@link ProcessFactory} interface.
 * 
 * @author John Chilton
 * 
 */
public class ProcessFactoryImpl implements ProcessFactory {
  private static final Log LOG = LogFactory.getLog(ProcessFactoryImpl.class);

  public Process createProcess(final ProcessConfiguration processConfiguration) {
    final List<String> command = new LinkedList<String>();
    final String application = processConfiguration.getApplication();
    Preconditions.checkNotNull(application, "Attempted to create process with null application.");
    command.add(application);
    final List<String> arguments = processConfiguration.getArguments();
    if(arguments != null) {
      command.addAll(processConfiguration.getArguments());
    }
    LOG.trace("Creating process builder with commands " + Joiner.on(" ").join(command) + " in directory " + processConfiguration.getDirectory());
    final ProcessBuilder processBuilder = new ProcessBuilder(command);
    if(processConfiguration.getEnvironment() != null) {
      for(final Entry<String, String> entry : processConfiguration.getEnvironment().entrySet()) {
        processBuilder.environment().put(entry.getKey(), entry.getValue());
      }
    }
    processBuilder.directory(processConfiguration.getDirectory());

    java.lang.Process baseProcess = null;
    try {
      baseProcess = processBuilder.start();
    } catch(final IOException e) {
      throw new IllegalStateException("Failed to start process corresponding to process configuration " + processConfiguration, e);
    }
    final ProcessImpl process = new ProcessImpl();
    process.setBaseProcess(baseProcess);
    return process;
  }

  private static class ProcessImpl implements Process {
    private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
    private java.lang.Process process;

    public void setBaseProcess(final java.lang.Process process) {
      this.process = process;
    }

    public void destroy() {
      IO_UTILS.closeQuietly(this.process.getErrorStream());
      IO_UTILS.closeQuietly(this.process.getInputStream());
      IO_UTILS.closeQuietly(this.process.getOutputStream());
      this.process.destroy();
    }

    public int exitValue() {
      return this.process.exitValue();
    }

    public InputStream getErrorStream() {
      return this.process.getErrorStream();
    }

    public InputStream getInputStream() {
      return this.process.getInputStream();
    }

    public OutputStream getOutputStream() {
      return this.process.getOutputStream();
    }

    public boolean isComplete() {
      boolean complete = true;
      try {
        this.process.exitValue();
      } catch(final Exception exception) {
        complete = false;
      }
      return complete;
    }

    public int waitFor() throws InterruptedException {
      return this.process.waitFor();
    }
  }
}
