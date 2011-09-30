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

import org.globus.axis.util.Util;

public class WindowsGramTest {
  static {
    Util.registerTransport();
  }

  /*
  @Test
  public void windowsGramTest() throws Exception {
    JobDescriptionType d = new JobDescriptionType();
    SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");    
    System.out.println(sdf.format(java.util.Calendar.getInstance().getTime()));
    d.setExecutable("c:\\WINDOWS\\system32\\hostname.exe");

    //final GlobusCredential cred = new GlobusCredential("C:\\Backup\\chilton-test-1.msi.umn.edu-cert.pem", "C:\\Backup\\chilton-test-1.msi.umn.edu-key.pem");
    final GlobusCredential cred = new GlobusCredential("C:\\Users\\Administrator\\Documents\\appdev0.msi.umn.edu-cert.pem", "C:\\Users\\Administrator\\Documents\\appdev0.msi.umn.edu-key.pem");

    DelegatedCredentialFactoryGridImpl impl = new DelegatedCredentialFactoryGridImpl();
    impl.setAllowedPartiesIterable(Arrays.asList("/C=US/O=UMN/OU=MSI/OU=caGrid/OU=Users/OU=Services/CN=host/chilton-test-1.msi.umn.edu", "/C=US/O=UMN/OU=MSI/OU=caGrid/OU=Users/OU=Services/CN=host/appdev0.msi.umn.edu"));
    impl.setCdsUrl("https://cagrid-cds.msi.umn.edu:8443/wsrf/services/cagrid/CredentialDelegationService");
    DelegatedCredentialReference ref = impl.createDelegatedCredential(Credentials.get(cred));

    DelegatedCredentialResolverGridImpl res = new DelegatedCredentialResolverGridImpl(); 
    res.setProxySupplier(Suppliers.ofInstance(Credentials.get(cred)));

    Credential cred2 = res.getDelgatedCredential(ref);

    final String serviceAddress = "https://chilton.msi.umn.edu:443/CampusGrid/GRAM/ManagedJobFactoryService.asmx";    
    final String factoryType = ManagedJobFactoryConstants.FACTORY_TYPE.FORK;
    final EndpointReferenceType factoryEndpoint = ManagedJobFactoryClientHelper.getFactoryEndpoint(serviceAddress, factoryType);
    final GramJobFactoryGridImpl jobFactory = new GramJobFactoryGridImpl();
    jobFactory.setTargetGramDotNet(true);
    final GramJob gramJob = jobFactory.getGramJob(d);
    gramJob.setCredentials(cred2);

    gramJob.setDelegationEnabled(false);
    final String submissionId = "uuid:" + UUIDGenFactory.getUUIDGen().nextUUID();
    gramJob.submit(factoryEndpoint, false, true, submissionId);

    Thread.sleep(1);
    System.out.println("Getting handle");
    System.out.println("Handle is " + gramJob.getHandle());
    System.out.println("Handle obtained");

    gramJob.refreshStatus();
  }
  */
}
