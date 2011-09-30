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

package edu.umn.msi.tropix.grid.io.transfer.impl;

import java.io.InputStream;

import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InvertedInputContextImpl;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.StreamOutputContextImpl;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.io.transfer.TransferContextFactory;
import edu.umn.msi.tropix.grid.io.transfer.TransferUtils;

public class TransferContextFactoryImpl implements TransferContextFactory<Credential> {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private TransferUtils transferUtils = new TransferUtilsGridImpl();

  public InputContext getDownloadContext(final TransferServiceContextReference tscRef, final Credential proxy) {
    return new InvertedInputContextImpl() {
      public void get(final OutputContext outputContext) {
        InputStream inputStream;
        try {
          inputStream = transferUtils.get(tscRef, proxy.getGlobusCredential());
        } catch(Exception e) {
          throw new RuntimeException(e);
        }
        try {
          outputContext.put(inputStream);
        } finally {
          IO_UTILS.closeQuietly(inputStream);
        }
      }
    };
  }

  public OutputContext getUploadContext(final TransferServiceContextReference tscRef, final Credential proxy) {
    return new StreamOutputContextImpl() {
      public void put(final InputStream inputStream) {
        try {
          transferUtils.put(inputStream, tscRef, proxy.getGlobusCredential());
        } catch(Exception e) {
          throw new RuntimeException(e);
        }          
      }
    };
  }

  public void setTransferUtils(final TransferUtils transferUtils) {
    this.transferUtils = transferUtils;
  }

}
