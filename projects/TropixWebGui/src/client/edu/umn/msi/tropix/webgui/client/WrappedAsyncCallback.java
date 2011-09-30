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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author John Chilton
 * 
 * @param <S>
 *          Generic type of wrapped AsyncCallback.
 * @param <T>
 *          Generic type of this AsyncCallback.
 */
public abstract class WrappedAsyncCallback<S, T> implements AsyncCallback<T> {
  private final AsyncCallback<S> wrappedCallback;

  public WrappedAsyncCallback(final AsyncCallback<S> wrappedCallback) {
    this.wrappedCallback = wrappedCallback;
  }

  public void onFailure(final Throwable throwable) {
    getWrappedCallback().onFailure(throwable);
  }

  public abstract void onSuccess(T result);

  protected AsyncCallback<S> getWrappedCallback() {
    return wrappedCallback;
  }

}
