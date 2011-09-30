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

package edu.umn.msi.tropix.common.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.execution.process.Process;
import edu.umn.msi.tropix.common.execution.process.ProcessConfiguration;
import edu.umn.msi.tropix.common.execution.process.impl.ProcessFactoryImpl;

// Tests ProcessImpl and ProcessFunctionImpl on linux systems
public class ProcessFactoryImplTest {
  private final boolean doTest;

  public ProcessFactoryImplTest() {
    this.doTest = System.getProperty("os.name").equals("Linux");
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class, timeOut = 1000)
  public void invalid() {
    if(doTest) {
      final ProcessFactoryImpl processFactory = new ProcessFactoryImpl();
      final ProcessConfiguration executionConfiguration = new ProcessConfiguration();
      executionConfiguration.setApplication("/mostlikelynotanapplication");
      processFactory.createProcess(executionConfiguration);
    }
  }

  // Tests arguments, application, and input stream.
  @Test(groups = "unit", timeOut = 1000)
  public void arguments() throws InterruptedException, IOException {
    if(doTest) {
      final ProcessFactoryImpl processFactory = new ProcessFactoryImpl();
      final ProcessConfiguration executionConfiguration = new ProcessConfiguration();
      executionConfiguration.setApplication("/bin/echo");
      executionConfiguration.setArguments(Arrays.asList(new String[] {"Hello", "World!"}));
      final Process process = processFactory.createProcess(executionConfiguration);
      final int exitVal = process.waitFor();
      assert exitVal == 0;
      final String str = IOUtils.toString(process.getInputStream());
      assert str.trim().equals("Hello World!");
    }
  }

  @Test(groups = "unit")
  public void nullArguments() throws IOException, InterruptedException {
    if(doTest) {
      final ProcessFactoryImpl processFactory = new ProcessFactoryImpl();
      final ProcessConfiguration executionConfiguration = new ProcessConfiguration();
      executionConfiguration.setArguments(Lists.<String>newArrayList());
      executionConfiguration.setApplication("pwd");
      executionConfiguration.setDirectory(new File("/tmp"));
      final Process process = processFactory.createProcess(executionConfiguration);
      final int exitVal = process.waitFor();
      assert exitVal == 0;
      final String str = IOUtils.toString(process.getInputStream());
      assert str.trim().equals("/tmp");
      assert "".equals(IOUtils.toString(process.getErrorStream()));
    }
  }

  // Tests working directory works correctly
  @Test(groups = {"unit"}, timeOut = 1000)
  public void directory() throws InterruptedException, IOException {
    if(doTest) {
      final ProcessFactoryImpl processFactory = new ProcessFactoryImpl();
      final ProcessConfiguration executionConfiguration = new ProcessConfiguration();
      executionConfiguration.setApplication("pwd");
      executionConfiguration.setDirectory(new File("/tmp"));
      final Process process = processFactory.createProcess(executionConfiguration);
      final int exitVal = process.waitFor();
      assert exitVal == 0;
      final String str = IOUtils.toString(process.getInputStream());
      assert str.trim().equals("/tmp");
      assert "".equals(IOUtils.toString(process.getErrorStream()));
    }
  }

  // Tests environment variables are exported
  @Test(groups = {"unit"}, timeOut = 1000)
  public void environment() throws InterruptedException, IOException {
    if(doTest) {
      final ProcessFactoryImpl processFactory = new ProcessFactoryImpl();
      final ProcessConfiguration executionConfiguration = new ProcessConfiguration();
      executionConfiguration.setApplication("env");
      final Map<String, String> env = new HashMap<String, String>();
      env.put("MOO", "COW");
      executionConfiguration.setEnvironment(env);
      final Process process = processFactory.createProcess(executionConfiguration);
      assert process.waitFor() == 0;
      assert IOUtils.toString(process.getInputStream()).contains("MOO=COW");
    }
  }

  // Test standard input
  @Test(groups = {"unit"}, timeOut = 1000)
  public void input() throws IOException, InterruptedException {
    if(doTest) {
      final ProcessFactoryImpl processFactory = new ProcessFactoryImpl();
      final ProcessConfiguration executionConfiguration = new ProcessConfiguration();
      executionConfiguration.setApplication("cat");
      final Process process = processFactory.createProcess(executionConfiguration);
      process.getOutputStream().write("Hello World!".getBytes());
      process.getOutputStream().close();
      assert process.waitFor() == 0;
      assert IOUtils.toString(process.getInputStream()).startsWith("Hello World!");
    }
  }

  @Test(groups = {"unit"}, timeOut = 1000)
  public void nonZeroExit() throws IOException, InterruptedException {
    if(doTest) {
      final ProcessFactoryImpl processFactory = new ProcessFactoryImpl();
      final ProcessConfiguration executionConfiguration = new ProcessConfiguration();
      executionConfiguration.setApplication("ls");
      executionConfiguration.setArguments(Arrays.asList("-e")); // ls doesn't
      // accept a -e
      // argument
      final Process process = processFactory.createProcess(executionConfiguration);
      assert process.waitFor() != 0;
    }
  }

  @Test(groups = {"unit"}, timeOut = 1000)
  public void complete() throws IOException, InterruptedException {
    if(doTest) {
      final ProcessFactoryImpl processFactory = new ProcessFactoryImpl();
      final ProcessConfiguration executionConfiguration = new ProcessConfiguration();
      executionConfiguration.setApplication("cat");
      final Process process = processFactory.createProcess(executionConfiguration);
      assert !process.isComplete();
      Thread.sleep(10);
      assert !process.isComplete();
      process.getOutputStream().close();
      Thread.sleep(10);
      while(!process.isComplete()) {
        Thread.sleep(10);
      }
      assert process.exitValue() == 0;
    }
  }

  @Test(groups = {"unit"}, timeOut = 1000)
  public void destroy() throws IOException, InterruptedException {
    if(doTest) {
      final ProcessFactoryImpl processFactory = new ProcessFactoryImpl();
      final ProcessConfiguration executionConfiguration = new ProcessConfiguration();
      executionConfiguration.setApplication("cat");

      final Process process = processFactory.createProcess(executionConfiguration);

      assert !process.isComplete();
      Thread.sleep(50);
      assert !process.isComplete();

      process.destroy();
      // Verify this destroy eventually causes process to complete.
      while(!process.isComplete()) {
        Thread.sleep(1);
      }
    }
  }

}
