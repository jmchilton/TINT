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

//import java.util.Arrays;
//
//import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
//import org.globus.gsi.GlobusCredential;
//import org.testng.annotations.Test;
//
//import com.google.common.base.Suppliers;
//
//import edu.umn.msi.tropix.common.io.StagingDirectory;
//import edu.umn.msi.tropix.grid.credentials.Credential;
//import edu.umn.msi.tropix.grid.credentials.Credentials;
//import edu.umn.msi.tropix.grid.credentials.impl.DelegatedCredentialFactoryGridImpl;
//import edu.umn.msi.tropix.grid.credentials.impl.DelegatedCredentialResolverGridImpl;
//import edu.umn.msi.tropix.grid.gridftp.impl.GridFtpClientFactoryImpl;
//import edu.umn.msi.tropix.grid.io.impl.GridFtpCredentialedStagingDirectoryFactoryImpl;
//
//public class WindowsGridFtpTest {
//  
//  @Test 
//  public void gridFtpTest() throws Exception {
//    final GridFtpClientFactoryImpl factory = new GridFtpClientFactoryImpl("160.94.77.141", 2811, false);
//    //final GlobusCredential globusCredential = new GlobusCredential("C:\\chilton2.msi.umn.edu-cert.pem", "C:\\chilton2.msi.umn.edu-key.pem");
//    final GlobusCredential globusCredential = new GlobusCredential("C:\\Backup\\chilton-test-1.msi.umn.edu-cert.pem", "C:\\Backup\\chilton-test-1.msi.umn.edu-key.pem");
//    //final GlobusCredential globusCredential = new GlobusCredential("C:\\Users\\Administrator\\chiltonproxy");
//    DelegatedCredentialFactoryGridImpl impl = new DelegatedCredentialFactoryGridImpl();
//    impl.setAllowedPartiesIterable(Arrays.asList("/C=US/O=UMN/OU=MSI/OU=caGrid/OU=Users/OU=Services/CN=host/chilton-test-1.msi.umn.edu"));
//    impl.setCdsUrl("https://cagrid-cds.msi.umn.edu:8443/wsrf/services/cagrid/CredentialDelegationService");
//    //impl.setDelegationLifetime(3600);    
//    //impl.setIssuedCredentialLifetime(10000);
//    //impl.setIssuedCredentialPathLength(1);
//    DelegatedCredentialReference ref = impl.createDelegatedCredential(Credentials.get(globusCredential));
//    
//    DelegatedCredentialResolverGridImpl res = new DelegatedCredentialResolverGridImpl(); 
//    res.setProxySupplier(Suppliers.ofInstance(Credentials.get(globusCredential)));
//
//    
//    GridFtpCredentialedStagingDirectoryFactoryImpl sdFactory = new GridFtpCredentialedStagingDirectoryFactoryImpl();
//    sdFactory.setDeleteStagedFiles(false);
//    sdFactory.setGridFtpClientFactory(factory);
//    sdFactory.setSep("\\");
//    sdFactory.setTempDirectoryPath("C:\\GridFTPRoot");
//    
//    Credential cred2 = res.getDelgatedCredential(ref);
//    StagingDirectory directory = sdFactory.get(cred2);
//    directory.setup();
//    directory.getResourceNames(null);
//    
//    
//    /*
//    System.out.println(cred2.getGlobusCredential().getIdentity());
//    GridFtpClient client = factory.getGridFtpClient(cred2);
//    final String dir = "C:\\GridFTPRoot\\" + UUID.randomUUID().toString();
//    client.makeDir(dir);
//    
//    client = factory.getGridFtpClient(cred2);
//    
//    final ByteArrayInputStream stream = new ByteArrayInputStream("Hello World".getBytes());
//    //client.put(dir + "\\Cow" + UUID.randomUUID().toString(), stream);
//    for(GridFtpFile file : client.list(dir + "\\")) {
//      file.getPath();
//    }
//    final ByteArrayOutputStream oStream = new ByteArrayOutputStream();
//    client.get("Cow.txt", oStream);
//    System.out.println(new String(oStream.toByteArray()));
//    */
//    /*
//    client.setAuthorization(new IdentityAuthorization("/C=US/O=UMN/OU=MSI/OU=caGrid/OU=Users/OU=Services/CN=host/chilton-test-1.msi.umn.edu"));
//    client.authenticate(new GlobusGSSCredentialImpl(globusCredential, GSSCredential.INITIATE_AND_ACCEPT));
//    client.setProtectionBufferSize(16843);
//    client.setType(Session.MODE_STREAM);
//    client.setMode(GridFTPSession.);
//    client.setDataChannelAuthentication(DataChannelAuthentication.SELF);
//    client.setDataChannelProtection(GridFTPSession.PROTECTION_SAFE);
//    client.setPassive();
//    client.setLocalActive();
//    final File file = File.createTempFile("tpx", "tst");
//    FILE_UTILS.writeStringToFile(file, "Moo Cow");
//    // DataSink sink = new DataSinkStream(FILE_UTILS.getFileOutputStream(file));
//    final DataSource source = new DataSourceStream(FILE_UTILS.getFileInputStream(file));
//    client.makeDir("/tmp/cowdir");
//    client.put("/tmp/moo", source, null);
//    */
//  }
//  
//}
