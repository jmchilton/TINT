/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.common.test.gram;

import java.io.File;
import java.io.IOException;

import org.globus.ftp.DataChannelAuthentication;
import org.globus.ftp.DataSource;
import org.globus.ftp.DataSourceStream;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.GridFTPSession;
import org.globus.ftp.Session;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.gsi.gssapi.auth.IdentityAuthorization;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;

public class GridFTPClientTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  @Test
  public void moo() throws GlobusCredentialException, ServerException, IOException, ClientException, GSSException {
    final GridFTPClient client = new GridFTPClient("haze.msi.umn.edu", 2811);
    final GlobusCredential globusCredential = new GlobusCredential("/home/john/.tropix/test/host-cert.pem", "/home/john/.tropix/test/host-key.pem");

    final String identity = globusCredential.getIdentity();
    System.out.println("Identity is " + identity);

    client.setAuthorization(new IdentityAuthorization("/C=US/O=UMN/OU=MSI/OU=caGrid/OU=Users/OU=Services/CN=host/haze.msi.umn.edu"));
    client.authenticate(new GlobusGSSCredentialImpl(globusCredential, GSSCredential.INITIATE_AND_ACCEPT));
    client.setProtectionBufferSize(16843);
    client.setType(Session.MODE_STREAM);
    client.setMode(GridFTPSession.MODE_EBLOCK);
    client.setDataChannelAuthentication(DataChannelAuthentication.SELF);
    client.setDataChannelProtection(GridFTPSession.PROTECTION_SAFE);
    client.setPassive();
    client.setLocalActive();
    final File file = File.createTempFile("tpx", "tst");
    FILE_UTILS.writeStringToFile(file, "Moo Cow");
    // DataSink sink = new DataSinkStream(FILE_UTILS.getFileOutputStream(file));
    final DataSource source = new DataSourceStream(FILE_UTILS.getFileInputStream(file));
    client.makeDir("/tmp/cowdir");
    client.put("/tmp/moo", source, null);
  }
}
