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

package edu.umn.msi.tropix.storage.client.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.StreamInputContextImpl;
import edu.umn.msi.tropix.common.io.StreamOutputContextImpl;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.storage.client.ModelStorageDataFactory;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class MockModelStorageDataFactoryImpl implements ModelStorageDataFactory {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  private static final class IdAndLocation {
    private final String id;
    private final String storageServiceUrl;

    IdAndLocation(final String id, final String storageServiceUrl) {
      Preconditions.checkNotNull(id);
      Preconditions.checkNotNull(storageServiceUrl);
      this.id = id;
      this.storageServiceUrl = storageServiceUrl;
    }
    
    public String toString() {
      return "IdAndLocation[id=" + id +  ",url=" + storageServiceUrl + "]"; 
    }

    @Override
    public int hashCode() {
      return id.hashCode() + 31 * storageServiceUrl.hashCode() + 17;
    }

    @Override
    public boolean equals(final Object other) {
      Preconditions.checkState(other instanceof IdAndLocation); // Private class will never be compared to other classes.
      final IdAndLocation otherIdAndLocation = (IdAndLocation) other;
      return otherIdAndLocation.id.equals(id) && otherIdAndLocation.storageServiceUrl.equals(storageServiceUrl);
    }

  }

  private class MockModelStorageDataImpl implements ModelStorageData {
    private final TransferResource downloadResource = new TransferResource(UUID.randomUUID().toString());
    private final TransferResource uploadResource = new TransferResource(UUID.randomUUID().toString());
    private final String id;
    private final TropixFile file;
    private final String storageServiceUrl;
    private byte[] data;

    MockModelStorageDataImpl(final String id, final String storageServiceUrl) {
      this.id = id;
      this.storageServiceUrl = storageServiceUrl;
      final TropixFile file = new TropixFile();
      file.setId(UUID.randomUUID().toString());
      file.setFileId(id);
      file.setStorageServiceUrl(storageServiceUrl);
      this.file = file;
    }

    MockModelStorageDataImpl(final TropixFile file) {
      this.id = file.getFileId();
      this.storageServiceUrl = file.getStorageServiceUrl();
      this.file = file;
    }

    public TropixFile getTropixFile() {
      return file;
    }

    public String getDataIdentifier() {
      return id;
    }

    public InputContext getDownloadContext() {
      return new StreamInputContextImpl() {
        public void get(final OutputStream outputStream) {
          Preconditions.checkNotNull(data, "No data for " + MockModelStorageDataImpl.this);
          IO_UTILS.copy(new ByteArrayInputStream(data), outputStream);
        }
      };
    }

    public OutputContext getUploadContext() {
      return new StreamOutputContextImpl() {
        public void put(final InputStream inputStream) {
          data = IO_UTILS.toByteArray(inputStream);
        }
      };
    }

    public TransferResource prepareDownloadResource() {
      return downloadResource;
    }

    public TransferResource prepareUploadResource() {
      return uploadResource;
    }

    private IdAndLocation getIdAndLocation() {
      return new IdAndLocation(getDataIdentifier(), storageServiceUrl);
    }
    
    public int hashCode() {
      return getIdAndLocation().hashCode();
    }
    
    public boolean equals(final Object other) {
      if(!(other instanceof MockModelStorageDataImpl)) {
        return false;
      }
      final MockModelStorageDataImpl otherData = (MockModelStorageDataImpl) other;
      return getIdAndLocation().equals(otherData.getIdAndLocation());
    }

  }

  private final Map<IdAndLocation, MockModelStorageDataImpl> storedData = Maps.newHashMap();
  private final List<MockModelStorageDataImpl> newlyIssuedStorageDataObjects = Lists.newArrayList();

  public MockModelStorageDataImpl getStorageData(final TropixFile file, final Credential credential) {
    Preconditions.checkNotNull(credential);
    final IdAndLocation idAndLocation = getIdAndLocation(file);
    if(hasMatching(idAndLocation)) {
      addToStoredData(new MockModelStorageDataImpl(file));
    }
    return storedData.get(idAndLocation);
  }

  private void addToStoredData(final MockModelStorageDataImpl storageData) {
    storedData.put(storageData.getIdAndLocation(), storageData);
  }
  
  private boolean hasMatching(final IdAndLocation idAndLocation) {
    return !storedData.containsKey(idAndLocation);
  }

  private IdAndLocation getIdAndLocation(final TropixFile file) {
    final IdAndLocation idAndLocation = new IdAndLocation(file.getFileId(), file.getStorageServiceUrl());
    return idAndLocation;
  }

  public MockModelStorageDataImpl getStorageData(final String dataIdentifier, final String serviceUrl, final Credential credential) {
    Preconditions.checkNotNull(credential);
    final IdAndLocation idAndLocation = new IdAndLocation(dataIdentifier, serviceUrl);
    if(hasMatching(idAndLocation)) {
      addToStoredData(new MockModelStorageDataImpl(dataIdentifier, serviceUrl));
    }
    return storedData.get(idAndLocation);
  }

  public ImmutableList<MockModelStorageDataImpl> getNewlyIssuedStorageDataObjects() {
    return ImmutableList.copyOf(newlyIssuedStorageDataObjects);
  }

  public MockModelStorageDataImpl getNewlyIssuedStorageDataObject() {
    return Iterables.getOnlyElement(newlyIssuedStorageDataObjects);
  }

  public MockModelStorageDataImpl getStorageData(final String serviceUrl, final Credential credential) {
    Preconditions.checkNotNull(credential);
    final MockModelStorageDataImpl storageData = new MockModelStorageDataImpl(UUID.randomUUID().toString(), serviceUrl);
    addToStoredData(storageData);
    newlyIssuedStorageDataObjects.add(storageData);
    return storageData;
  }
  

}
