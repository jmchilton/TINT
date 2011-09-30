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

package edu.umn.msi.tropix.storage.service.impl;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource;
import org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResourceHome;
import org.cagrid.transfer.context.service.helper.DataStagedCallback;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataDescriptor;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.utils.AddressingUtils;

import com.google.common.collect.MapMaker;

import gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils;

public class UploadTransferServiceContextFactory {
  private static final Log LOG = LogFactory.getLog(UploadTransferServiceContextFactory.class);

  /**
   * A concurrent map that holds "strong" references to objects that lets go of the references after 31 minutes.
   */
  private static final ConcurrentMap<TransferServiceContextResource, TransferServiceContextResource> RESOURCE_REFERENCE_MAP = new MapMaker().expiration(31 * 60, TimeUnit.SECONDS).makeMap();

  private static void storeReference(final TransferServiceContextResource resource) {
    // This is Map is being used as a Set, ignore the fact that it has a key and value.
    RESOURCE_REFERENCE_MAP.put(resource, resource);
  }

  /**
   * This method is nearly identical to TransferServiceHelper.createTransferContext(DataDescriptor, DataStagedCallback) but makes sure the resource objects do not expire until after the corresponding context has been terminated.
   * 
   * @param dd
   * @param callback
   * @return
   * @throws RemoteException
   */
  public static TransferServiceContextReference createTransferContext(final DataDescriptor dd, final DataStagedCallback callback) throws RemoteException {
    EndpointReferenceType epr = null; // new org.apache.axis.message.addressing.EndpointReferenceType(); - created object was never used

    final String homeName = "java:comp/env/services/cagrid/TransferServiceContext/home";
    try {
      final Context initialContext = new InitialContext();
      final TransferServiceContextResourceHome home = (TransferServiceContextResourceHome) initialContext.lookup(homeName);
      final ResourceKey resourceKey = home.createResource();

      // Grab the newly created resource
      final TransferServiceContextResource thisResource = (TransferServiceContextResource) home.find(resourceKey);
      thisResource.setSecurityDescriptor(SecurityUtils.createCreatorOnlyResourceSecurityDescriptor());
      LOG.debug("Calling stage on resource with callback " + callback);
      thisResource.stage(dd, callback);
      String transportURL = (String) MessageContext.getCurrentContext().getProperty(org.apache.axis.MessageContext.TRANS_URL);
      transportURL = transportURL.substring(0, transportURL.lastIndexOf('/') + 1);
      transportURL += "TransferServiceContext";
      epr = AddressingUtils.createEndpointReference(transportURL, resourceKey);
      storeReference(thisResource);
    } catch(final Exception e) {
      throw new RemoteException("Error looking up TransferServiceContext home:" + e.getMessage(), e);
    }

    // return the typed EPR
    final TransferServiceContextReference ref = new TransferServiceContextReference();
    ref.setEndpointReference(epr);
    LOG.debug("Returning ref");
    return ref;
  }

}
