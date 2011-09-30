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

package edu.umn.msi.tropix.webgui.client;

import javax.annotation.Nullable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;

import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils.Cleanable;

/**
 * This is a base class for most of the callbacks issued in this 
 * module. It serves as a common place to handle errors in a uniform
 * way, it has convenience methods for cleaning up resources, and
 * allows this
 * 
 * new AyncCallback<BigLongTypeDescription>() {
 *   public void onSuccess(final BigLongTypeDescription result) {
 *     doSomething(result);
 *   }
 * }
 * 
 * to be replaced by (notice that BigLongTypeDescription only appears 
 * once.) 
 * 
 * new AyncCallbackImpl<BigLongTypeDescription>() {
 *   public void handleSuccess() {
 *     doSomething(getResult());
 *   }
 * }

 * @author John Chilton
 *
 * @param <T> Type of result
 */
public class AsyncCallbackImpl<T> implements AsyncCallback<T> {
  private static final String DEFAULT_MESSAGE = "Error communicating with server, please contact Tropix staff.";
  private String message;
  private Cleanable cleanable;
  private boolean cleanUpOnSuccess = true;
  
  public AsyncCallbackImpl() {
    this(DEFAULT_MESSAGE);
  }

  public AsyncCallbackImpl(final String message) {
    this(message, null);
  }

  public AsyncCallbackImpl(final Cleanable cleanable) {
    this(DEFAULT_MESSAGE, cleanable);
  }

  public AsyncCallbackImpl(final Canvas canvas) {
    this(new Cleanable() {
      public void close() {
        canvas.destroy();
      }      
    });
  }
  
  public AsyncCallbackImpl<T> noCleanUpOnSuccess() {
    this.cleanUpOnSuccess = false;
    return this;
  }

  public AsyncCallbackImpl(final String message, @Nullable final Cleanable cleanable) {
    this.message = message;
    this.cleanable = cleanable;
  }

  private void cleanUp() {
    if(cleanable != null) {
      cleanable.close();
    }
  }

  public void onFailure(final Throwable caught) {
    cleanUp();
    SC.say(message);
    GWT.getUncaughtExceptionHandler().onUncaughtException(caught);
  }
  
  protected void handleSuccess() {
    
  }
  
  private T result = null;

  protected T getResult() {
    return result;
  }
  
  public void onSuccess(final T result) {
    try {
      this.result = result;
      handleSuccess();
    } catch(RuntimeException e) {
      GWT.getUncaughtExceptionHandler().onUncaughtException(e);
    }
    if(cleanUpOnSuccess) {
      cleanUp();
    }
  }
}
