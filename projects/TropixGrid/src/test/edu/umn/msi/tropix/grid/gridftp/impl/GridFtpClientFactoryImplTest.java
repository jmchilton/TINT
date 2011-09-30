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

package edu.umn.msi.tropix.grid.gridftp.impl;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.grid.credentials.Credentials;

/**
 * Really very little unit testing can be done on {@link GridFtpClientFactoryImpl}.
 * 
 * @author John Chilton
 *
 */
public class GridFtpClientFactoryImplTest {

  @Test(groups = "unit")
  public void testParameters() {
    final String host = "http://cow";
    final int port = 25;
    
    GridFtpClientFactoryImpl factory = new GridFtpClientFactoryImpl(host, port);
    GridFtpClientImpl client = factory.getGridFtpClient(Credentials.getMock());
    assert client.getPort() == port;
    assert client.getHost().equals(host);
    assert client.getEnableDataChannelProtection();

    // Now without data channel protection
    factory = new GridFtpClientFactoryImpl(host, port, false);
    client = factory.getGridFtpClient(Credentials.getMock());
    assert client.getPort() == port;
    assert client.getHost().equals(host);
    assert !client.getEnableDataChannelProtection();
  }
  
}
