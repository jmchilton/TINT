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

import edu.umn.msi.tropix.common.collect.Closures;
import edu.umn.msi.tropix.common.collect.Collections;
import edu.umn.msi.tropix.common.collect.Predicates2;
import edu.umn.msi.tropix.common.collect.StringPredicates;
import edu.umn.msi.tropix.common.concurrent.Executors;
import edu.umn.msi.tropix.common.concurrent.InitializationTrackers;
import edu.umn.msi.tropix.common.concurrent.InterruptableExecutors;
import edu.umn.msi.tropix.common.concurrent.PausableStateTrackers;
import edu.umn.msi.tropix.common.concurrent.Timers;
import edu.umn.msi.tropix.common.execution.process.Processes;
import edu.umn.msi.tropix.common.io.Directories;
import edu.umn.msi.tropix.common.io.FileFunctions;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOFunctions;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.LineProcessors;
import edu.umn.msi.tropix.common.io.StagingDirectories;
import edu.umn.msi.tropix.common.io.StreamCopiers;
import edu.umn.msi.tropix.common.io.TempFileSuppliers;
import edu.umn.msi.tropix.common.io.ZipUtilsFactory;
import edu.umn.msi.tropix.common.io.impl.FileDisposableResourceFactory;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.common.logging.IntermittentLogs;
import edu.umn.msi.tropix.common.prediction.Models;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
import edu.umn.msi.tropix.common.shutdown.ShutdownException;

public class CommonConstructorTest extends ConstructorTest {
  {
    this.getClasses().add(FileDisposableResourceFactory.class);
    this.getClasses().add(ReflectionHelpers.class);
    this.getClasses().add(Closures.class);
    this.getClasses().add(InputContexts.class);
    this.getClasses().add(FileFunctions.class);
    this.getClasses().add(IOUtilsFactory.class);
    this.getClasses().add(ZipUtilsFactory.class);
    this.getClasses().add(FileUtilsFactory.class);
    this.getClasses().add(LineProcessors.class);
    this.getClasses().add(Collections.class);
    this.getClasses().add(InterruptableExecutors.class);
    this.getClasses().add(ExceptionUtils.class);
    this.getClasses().add(Executors.class);
    this.getClasses().add(InitializationTrackers.class);
    this.getClasses().add(PausableStateTrackers.class);
    this.getClasses().add(Timers.class);
    this.getClasses().add(TempFileSuppliers.class);
    this.getClasses().add(StagingDirectories.class);
    this.getClasses().add(StreamCopiers.class);
    this.getClasses().add(Processes.class);
    this.getClasses().add(ShutdownException.class);
    this.getClasses().add(Models.class);
    this.getClasses().add(IntermittentLogs.class);
    this.getClasses().add(Directories.class);
    this.getClasses().add(IOFunctions.class);
    this.getClasses().add(EasyMockUtils.class);
    this.getClasses().add(StringPredicates.class);
    this.getClasses().add(Predicates2.class);
    this.getClasses().add(FileFunctions.class);
  }
}