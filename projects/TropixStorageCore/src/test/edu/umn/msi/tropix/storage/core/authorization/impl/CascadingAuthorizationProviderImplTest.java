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

package edu.umn.msi.tropix.storage.core.authorization.impl;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.storage.core.authorization.AuthorizationProvider;

public class CascadingAuthorizationProviderImplTest {
  private CascadingAuthorizationProviderImpl authorizer = null;
  private String id = null, caller = null;

  @BeforeMethod(groups = "unit")
  public void init() {
    authorizer = new CascadingAuthorizationProviderImpl();
    id = UUID.randomUUID().toString();
    caller = UUID.randomUUID().toString();
  }

  private static class FixedAuthorizationProviderImpl implements AuthorizationProvider {
    private final Boolean download, upload, delete;

    FixedAuthorizationProviderImpl(final Boolean answer) {
      this(answer, answer, answer);
    }

    FixedAuthorizationProviderImpl(final Boolean download, final Boolean upload, final Boolean delete) {
      this.download = download;
      this.upload = upload;
      this.delete = delete;
    }

    public Boolean canDelete(final String id, final String callerIdentity) {
      return delete;
    }

    public Boolean canDownload(final String id, final String callerIdentity) {
      return download;
    }

    public Boolean canUpload(final String id, final String callerIdentity) {
      return upload;
    }
    
    public Boolean canDownloadAll(final String[] ids, final String callerIdentity) {
      return download;
    }

  }

  private static class NullAuthorizer extends FixedAuthorizationProviderImpl {
    NullAuthorizer() {
      super(null);
    }
  }

  private static class TrueAuthorizer extends FixedAuthorizationProviderImpl {
    TrueAuthorizer() {
      super(true);
    }
  }

  private static class FalseAuthorizer extends FixedAuthorizationProviderImpl {
    FalseAuthorizer() {
      super(false);
    }
  }

  @Test(groups = "unit")
  public void order() {

    List<AuthorizationProvider> providers = Lists.<AuthorizationProvider>newArrayList(new TrueAuthorizer(), new FalseAuthorizer());
    authorizer.setAuthorizationProviders(providers);
    assert authorizer.canDelete(id, caller);
    assert authorizer.canUpload(id, caller);
    assert authorizer.canDownload(id, caller);

    providers = Lists.<AuthorizationProvider>newArrayList(new FalseAuthorizer(), new TrueAuthorizer());
    authorizer.setAuthorizationProviders(providers);
    assert !authorizer.canDelete(id, caller);
    assert !authorizer.canUpload(id, caller);
    assert !authorizer.canDownload(id, caller);
  }

  @Test(groups = "unit")
  public void delegation() {

    final AuthorizationProvider provider = new FixedAuthorizationProviderImpl(true, false, null);

    // Verify it is respecting null return for delete by testing delete
    // respects default.
    authorizer.setDeleteDefault(true);
    authorizer.setAuthorizationProviders(Lists.newArrayList(provider));
    assert authorizer.canDelete(id, caller);
    authorizer.setDeleteDefault(false);
    assert !authorizer.canDelete(id, caller);

    // Verify it is respecting true return of canDownload by verifying canDownload
    // regardless of default.
    authorizer.setReadDefault(true);
    assert authorizer.canDownload(id, caller);
    authorizer.setReadDefault(false);
    assert authorizer.canDownload(id, caller);

    // Verify it is respecting true return of canDownload by verifying not canUpload
    // regardless of default.
    authorizer.setWriteDefault(true);
    assert !authorizer.canUpload(id, caller);
    authorizer.setWriteDefault(false);
    assert !authorizer.canUpload(id, caller);
  }

  @Test(groups = "unit")
  public void defaults() {
    final List<AuthorizationProvider> providers = Lists.newArrayListWithCapacity(2);
    for(int i = 0; i < 2; i++) {
      authorizer.setDeleteDefault(true);
      assert authorizer.canDelete(id, caller);
      authorizer.setDeleteDefault(false);
      assert !authorizer.canDelete(id, caller);

      authorizer.setReadDefault(true);
      assert authorizer.canDownload(id, caller);
      authorizer.setReadDefault(false);
      assert !authorizer.canDownload(id, caller);

      authorizer.setReadDefault(true);
      assert authorizer.canDownloadAll(new String[] {id}, caller);
      authorizer.setReadDefault(false);
      assert !authorizer.canDownload(id, caller);
      
      authorizer.setWriteDefault(true);
      assert authorizer.canUpload(id, caller);
      authorizer.setWriteDefault(false);
      assert !authorizer.canUpload(id, caller);

      providers.add(new NullAuthorizer());
      authorizer.setAuthorizationProviders(providers);
    }
  }

}
