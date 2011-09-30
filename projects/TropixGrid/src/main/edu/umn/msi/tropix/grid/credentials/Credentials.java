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

package edu.umn.msi.tropix.grid.credentials;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.grid.credentials.impl.CredentialImpl;

/**
 * Factory methods for producing instances of the {@link Credential} interface.
 * 
 * @author John Chilton
 * 
 */
public class Credentials {
  private static final Pattern MOCK_CREDENTIAL_SERIAL_PATTERN = Pattern.compile("MockCredential\\[([^,]+),(.+)\\]");
  private static final long DEFAULT_TIME = 12 * 60 * 60;
  private static final GlobusCredentialFactory GLOBUS_CREDENTIAL_FACTORY = GlobusCredentialFactories.getInstance();
  /**
   * 
   * @return A Credential that wraps the default globus credential.
   */
  @Nonnull
  public static Credential getDefaultCredential() {
    try {
      return Credentials.get(GlobusCredential.getDefaultCredential());
    } catch(final GlobusCredentialException e) {
      throw ExceptionUtils.convertException(e, "Failed to obtain default credential.");
    }
  }
  
  @Nonnull
  public static Credential get(@Nonnull final GlobusCredential proxy) {
    return new CredentialImpl(proxy);
  }

  @Nonnull
  public static Credential getMock(@Nonnull final String identity) {
    return getMock(identity, DEFAULT_TIME);
  }

  @Nonnull
  public static Credential getMock(@Nonnull final String identity, final long length) {
    return new MockCredential(identity, System.currentTimeMillis() / 1000 + length);
  }

  @Nonnull
  public static Credential getMock() {
    return getMock(UUID.randomUUID().toString());
  }

  @Nonnull
  public static Credential fromString(final String serializedCredential) {
    final Matcher mockMatcher = MOCK_CREDENTIAL_SERIAL_PATTERN.matcher(serializedCredential);
    if(mockMatcher.matches()) {
      return new MockCredential(mockMatcher.group(1), mockMatcher.group(2), DEFAULT_TIME);
    } else {
      try {
        return get(GLOBUS_CREDENTIAL_FACTORY.get(new ByteArrayInputStream(serializedCredential.getBytes())));
      } catch(final Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Immutable
  private static final class MockCredential implements Credential, Serializable {
    @Nonnull
    private final String identity, uniqueKey;

    private final long expireTime;

    private MockCredential(@Nonnull final String identity, @Nonnull final String uniqueKey, final long expireTime) {
      this.identity = identity;
      this.uniqueKey = uniqueKey;
      this.expireTime = expireTime;
    }

    private MockCredential(final String identity, final long expireTime) {
      this(identity, UUID.randomUUID().toString(), expireTime);
      Preconditions.checkArgument(!identity.contains(","));
    }

    @Nullable
    public GlobusCredential getGlobusCredential() {
      return null;
    }

    @Nonnull
    public String getIdentity() {
      return identity;
    }

    public int hashCode() {
      return identity.hashCode() + uniqueKey.hashCode();
    }

    public boolean equals(@Nullable final Object other) {
      boolean equal = false;
      if(other instanceof MockCredential) {
        final MockCredential credential = (MockCredential) other;
        equal = credential.uniqueKey.equals(this.uniqueKey) && credential.identity.equals(this.identity);
      }
      return equal;
    }

    @Nonnull
    public byte[] toBytes() {
      return toString().getBytes();
    }
        
    @Nonnull
    public String toString() {
      return "MockCredential[" + identity + "," + uniqueKey + "]";
    }

    public long getTimeLeft() {
      return Math.max(expireTime - System.currentTimeMillis() / 1000, 0);
    }
  }

}
