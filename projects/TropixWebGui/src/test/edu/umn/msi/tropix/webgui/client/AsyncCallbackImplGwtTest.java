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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.smartgwt.client.widgets.layout.Layout;

public class AsyncCallbackImplGwtTest extends TropixGwtTestCase {
  private class DestroyDetectingLayout extends Layout {
    private boolean destroyed = false;

    @Override
    public void destroy() {
      destroyed = true;
      super.destroy();
    }
  }
  
  class TrackingAsyncCallbackImpl<T> extends AsyncCallbackImpl<T> {
    private boolean handleSuccessCalled = false;
    
    @Override
    protected void handleSuccess() {
      handleSuccessCalled = true;
    }
    
  }
  
  private class UncaughtExceptionHandlerImpl implements UncaughtExceptionHandler {
    private Throwable throwable;
    public void onUncaughtException(final Throwable throwable) {
      this.throwable = throwable;
    }
    
  }

  public void testClosedOnSuccess() {
    final DestroyDetectingLayout layout = new DestroyDetectingLayout();
    final AsyncCallbackImpl<String> callback = new AsyncCallbackImpl<String>(layout);
    callback.onSuccess("moo");
    assert layout.destroyed;
  }

  public void testClosedOnFailure() {
    final DestroyDetectingLayout layout = new DestroyDetectingLayout();
    final UncaughtExceptionHandlerImpl handler = new UncaughtExceptionHandlerImpl();
    final UncaughtExceptionHandler oldHandler = GWT.getUncaughtExceptionHandler();
    GWT.setUncaughtExceptionHandler(handler);
    final AsyncCallbackImpl<String> callback = new AsyncCallbackImpl<String>(layout);
    callback.onFailure(new RuntimeException());
    assert layout.destroyed;
    assert handler.throwable != null;
    GWT.setUncaughtExceptionHandler(oldHandler);
  }
  
  
}
