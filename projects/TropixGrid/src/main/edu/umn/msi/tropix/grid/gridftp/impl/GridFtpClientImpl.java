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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.ftp.DataChannelAuthentication;
import org.globus.ftp.DataSinkStream;
import org.globus.ftp.DataSourceStream;
import org.globus.ftp.FileInfo;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.GridFTPSession;
import org.globus.ftp.Session;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.ietf.jgss.GSSCredential;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClient;

@NotThreadSafe
class GridFtpClientImpl implements GridFtpClient {
  private static final Log LOG = LogFactory.getLog(GridFtpClientImpl.class);
  private final GlobusCredential proxy;
  private final String host;
  private final int port;
  private final boolean enableDataChannelProtection;

  @VisibleForTesting
  String getHost() {
    return host;
  }

  @VisibleForTesting
  int getPort() {
    return port;
  }

  @VisibleForTesting
  boolean getEnableDataChannelProtection() {
    return enableDataChannelProtection;
  }

  private GridFTPClient wrappedClient;

  private void init() {
    try {
      LOG.trace("Creating a GridFTPClient for host " + host + " and port " + port);
      wrappedClient = new GridFTPClient(host, port);
      wrappedClient.authenticate(new GlobusGSSCredentialImpl(proxy, GSSCredential.INITIATE_AND_ACCEPT));
      wrappedClient.setProtectionBufferSize(16843);
      wrappedClient.setType(Session.TYPE_IMAGE);
      wrappedClient.setMode(Session.MODE_STREAM);
      if(enableDataChannelProtection) {
        wrappedClient.setDataChannelAuthentication(DataChannelAuthentication.SELF);
        wrappedClient.setDataChannelProtection(GridFTPSession.PROTECTION_SAFE);
      } else {
        // This was added for GridFTP.NET
        wrappedClient.setDataChannelAuthentication(DataChannelAuthentication.NONE);
        wrappedClient.setDataChannelProtection(GridFTPSession.PROTECTION_CLEAR);
      }
    } catch(final Exception e) {
      throw ExceptionUtils.convertException(e, "Failed to initialize GridFTPClient");
    }
  }

  private void close() {
    try {
      wrappedClient.close(true);
    } catch(final Exception e) {
      ExceptionUtils.logQuietly(LOG, e);
    }
  }

  GridFtpClientImpl(final String host, final int port, final GlobusCredential proxy) {
    this(host, port, proxy, true);
  }

  GridFtpClientImpl(final String host, final int port, final GlobusCredential proxy, final boolean enableDataChannelProtection) {
    this.host = host;
    this.port = port;
    this.proxy = proxy;
    this.enableDataChannelProtection = enableDataChannelProtection;
  }

  public void deleteDir(final String dir) {
    init();
    try {
      LOG.trace("Deleting directory " + dir);
      wrappedClient.deleteDir(dir);
      LOG.trace("Directory deleted ");
    } catch(final Exception e) {
      throw ExceptionUtils.convertException(e);
    } finally {
      close();
    }
  }

  public void deleteFile(final String filename) {
    init();
    try {
      LOG.trace("Deleting file " + filename);
      wrappedClient.deleteFile(filename);
      LOG.trace("File " + filename + " deleted");
    } catch(final Exception e) {
      throw ExceptionUtils.convertException(e);
    } finally {
      close();
    }
  }

  public boolean exists(final String filename) {
    init();
    try {
      return wrappedClient.exists(filename);
    } catch(final Exception e) {
      throw ExceptionUtils.convertException(e);
    } finally {
      close();
    }
  }

  public void put(final String fileName, final InputStream inputStream) {
    init();
    try {
      wrappedClient.setPassive();
      wrappedClient.setLocalActive();
      wrappedClient.put(fileName, new DataSourceStream(inputStream), null);
    } catch(final Exception e) {
      throw ExceptionUtils.convertException(e);
    } finally {
      close();
    }
  }

  public void get(final String fileName, final OutputStream outputStream) {
    LOG.trace("getting file " + fileName);
    init();
    try {
      wrappedClient.setPassive();
      wrappedClient.setLocalActive();
      // wrappedClient.setLocalPassive();
      // wrappedClient.setActive();
      wrappedClient.get(fileName, new DataSinkStream(outputStream), null);
    } catch(final Exception e) {
      throw ExceptionUtils.convertException(e);
    } finally {
      close();
    }
  }

  public void makeDir(final String dir) {
    init();
    try {
      wrappedClient.makeDir(dir);
      try {
        wrappedClient.quote("SITE CHMOD 700 " + dir);
      } catch(Exception e) {
        LOG.warn("Failed to CHMOD directory " + dir);
      } // Ignore this exception, some GRIDFTP servers won't understand this command
    } catch(final Exception e) {
      throw ExceptionUtils.convertException(e);
    } finally {
      close();
    }
  }

  public Iterable<GridFtpFile> list(final String dir) {
    init();
    try {
      LOG.trace("In list, setting local passive");
      wrappedClient.setPassive();
      wrappedClient.setLocalActive();
      LOG.trace("Listing contents of " + dir);
      @SuppressWarnings("unchecked")
      final Vector<FileInfo> vector = wrappedClient.list(dir);
      LOG.trace("Contents obtained");
      final Builder<GridFtpFile> listBuilder = ImmutableList.builder();
      for(final FileInfo fileInfo : vector) {
        final String name = fileInfo.getName();
        if(name.equals(".") || name.equals("..")) {
          continue;
        }
        listBuilder.add(new GridFtpFile(dir + File.separator + name, fileInfo.isDirectory()));
      }
      return listBuilder.build();
    } catch(final Exception e) {
      throw ExceptionUtils.convertException(e);
    } finally {
      close();
    }
  }

}