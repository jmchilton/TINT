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

package edu.umn.msi.tropix.client.search.impl;

import info.minnesotapartnership.tropix.search.TropixSearchService;
import info.minnesotapartnership.tropix.search.models.Data;
import info.minnesotapartnership.tropix.search.models.File;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.client.directory.GridUser;
import edu.umn.msi.tropix.client.search.TropixSearchClient;
import edu.umn.msi.tropix.client.search.models.GridData;
import edu.umn.msi.tropix.client.search.models.GridFile;
import edu.umn.msi.tropix.common.collect.Collections;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;

public class TropixSearchClientImpl implements TropixSearchClient {
  private static final Log LOG = LogFactory.getLog(TropixSearchClientImpl.class);
  private GridServiceFactory<TropixSearchService> tropixSearchServiceFactory;
  private Iterable<GridUser> gridUserIterable;

  public List<GridData> getChildItems(final String serviceUrl, final Credential proxy, final String dataId) {
    final TropixSearchService searchService = tropixSearchServiceFactory.getService(serviceUrl, proxy);
    try {
      return transform(searchService.getChildren(dataId), serviceUrl);
    } catch(final RemoteException e) {
      throw ExceptionUtils.logAndConvert(LOG, e, "getChildItems for service " + serviceUrl + " and dataId " + dataId + " failed.");
    }
  }

  public List<GridData> getTopLevelItems(final String serviceUrl, final Credential proxy, final String ownerId) {
    final TropixSearchService searchService = tropixSearchServiceFactory.getService(serviceUrl, proxy);
    try {
      return transform(searchService.getUsersTopLevelData(ownerId), serviceUrl);
    } catch(final RemoteException e) {
      throw ExceptionUtils.logAndConvert(LOG, e, "getTopLevelData for service " + serviceUrl + " failed.");
    }
  }

  private List<GridData> transform(final Data[] inputData, final String serviceUrl) {
    final Data[] data = inputData == null ? new Data[0] : inputData;
    final List<GridData> results = new ArrayList<GridData>(data.length);
    Collections.transform(Arrays.asList(data), getConversionFunction(serviceUrl), results);
    final Multimap<String, GridData> gridDataByOwnerMap = HashMultimap.create();
    for(final GridData gridData : results) {
      gridDataByOwnerMap.put(gridData.getOwnerId(), gridData);
    }
    for(final GridUser user : gridUserIterable) {
      if(gridDataByOwnerMap.containsKey(user.getGridId())) {
        for(final GridData gridData : gridDataByOwnerMap.get(user.getGridId())) {
          gridData.setUserName(user.toString());
        }
      }
    }
    return results;
  }

  private Function<Data, GridData> getConversionFunction(final String serviceUrl) {
    return new Function<Data, GridData>() {
      public GridData apply(final Data data) {
        GridData gridData;
        if(data instanceof File) {
          final File file = (File) data;
          final GridFile gridFile = new GridFile();
          gridFile.setFileIdentifier(file.getFileIdentifier());
          gridFile.setStorageService(file.getFileStorageService());
          gridFile.setType(file.getFileType());
          gridFile.setTypeDescription(file.getFileTypeDescription());
          gridData = gridFile;
        } else {
          gridData = new GridData();
        }
        gridData.setOwnerId(data.getDataOwnerId());
        gridData.setServiceUrl(serviceUrl);
        gridData.setId(data.getDataId());
        gridData.setName(data.getDataName());
        gridData.setCreationDate(data.getDataCreationDate());
        gridData.setDescription(data.getDataDescription());
        gridData.setDataHasChildren(data.isDataHasChildren());
        return gridData;
      }
    };
  }

  public void setTropixSearchServiceFactory(final GridServiceFactory<TropixSearchService> tropixSearchServiceFactory) {
    this.tropixSearchServiceFactory = tropixSearchServiceFactory;
  }

  public void setGridUserIterable(final Iterable<GridUser> gridUserIterable) {
    this.gridUserIterable = gridUserIterable;
  }

}
