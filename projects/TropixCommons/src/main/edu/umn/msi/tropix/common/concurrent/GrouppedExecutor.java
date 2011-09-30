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

package edu.umn.msi.tropix.common.concurrent;

/**
 * This is like the Executor interface in java.util.concurrent, but in addition 
 * to taking a runnable takes a group so that implementations may determine like Runnables.
 * 
 * GrouppedFifoExecutorImpl is an example of this, and uses the the groups to 
 * ensure that only one runnable from a particular group runs at a time.
 * 
 * @author John Chilton
 * 
 * @param <G>
 *          Type describing the group used for grouping like runnables together.
 */
public interface GrouppedExecutor<G> {

  void execute(G group, Runnable runnable);

}
