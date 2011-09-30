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

package edu.umn.msi.tropix.common.concurrent.impl;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * An implementation of ThreadFactory customized for Tropix, that
 * can be manipulated via JMX when instatiated as a Spring bean.
 * 
 * @author John Chilton
 *
 */
@ManagedResource
public class ThreadFactoryImpl implements ThreadFactory {
  private ThreadGroup threadGroup = null;
  private Function<Runnable, String> nameFunction = null;
  private Function<Runnable, Integer> priorityFunction = null;
  private UncaughtExceptionHandler uncaughtExceptionHandler = null;
  private boolean daemon = false;

  public void setDaemon(final boolean daemon) {
    this.daemon = daemon;
  }

  public void setUncaughtExceptionHandler(final UncaughtExceptionHandler uncaughtExceptionHandler) {
    this.uncaughtExceptionHandler = uncaughtExceptionHandler;
  }

  public void setNameFunction(final Function<Runnable, String> nameFunction) {
    this.nameFunction = nameFunction;
  }

  public void setNameSupplier(final Supplier<String> nameSupplier) {
    final Function<Runnable, String> nameFunction = new Function<Runnable, String>() {
      public String apply(final Runnable arg0) {
        return nameSupplier.get();
      }
    };
    this.setNameFunction(nameFunction);
  }

  @ManagedOperation
  public void setName(final String name) {
    this.setNameSupplier(Suppliers.ofInstance(name));
  }

  public void setPriorityFunction(final Function<Runnable, Integer> priorityFunction) {
    this.priorityFunction = priorityFunction;
  }

  public void setPrioritySupplier(final Supplier<Integer> prioritySupplier) {
    final Function<Runnable, Integer> priorityFunction = new Function<Runnable, Integer>() {
      public Integer apply(final Runnable arg0) {
        return prioritySupplier.get();
      }
    };
    this.setPriorityFunction(priorityFunction);
  }

  @ManagedOperation
  public void setPriority(final int priority) {
    this.setPrioritySupplier(Suppliers.ofInstance(priority));
  }

  public Thread newThread(final Runnable runnable) {
    Thread thread;
    if(this.threadGroup != null) {
      thread = new Thread(this.threadGroup, runnable);
    } else {
      thread = new Thread(runnable);
    }
    if(this.nameFunction != null) {
      final String name = this.nameFunction.apply(runnable);
      if(name != null) {
        thread.setName(name);
      }
    }
    if(this.priorityFunction != null) {
      final Integer priority = this.priorityFunction.apply(runnable);
      if(priority != null) {
        thread.setPriority(priority);
      }
    }
    thread.setDaemon(this.daemon);
    if(this.uncaughtExceptionHandler != null) {
      thread.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
    }
    return thread;
  }

  public ThreadGroup getThreadGroup() {
    return this.threadGroup;
  }

  public void setThreadGroup(final ThreadGroup threadGroup) {
    this.threadGroup = threadGroup;
  }

}
