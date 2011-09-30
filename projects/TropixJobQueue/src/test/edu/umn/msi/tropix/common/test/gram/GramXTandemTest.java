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

import java.io.InputStream;

import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.globus.exec.generated.JobDescriptionType;
import org.globus.exec.generated.StateEnumeration;
import org.globus.exec.utils.ManagedJobFactoryConstants;
import org.globus.exec.utils.client.ManagedJobFactoryClientHelper;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.security.Constants;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramJob;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramJobFactoryGridImpl;
import edu.umn.msi.tropix.common.jobqueue.test.JobQueueTest;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class GramXTandemTest {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  @Test
  public void gramXTandemTest() throws Exception {
    // String xslPath = "/opt/parallel_tandem_08-02-01-3/bin/tandem-style.xsl", xtandemPath = "/opt/parallel_tandem_08-02-01-3/bin/tandem.exe", workingDirectory = "/opt/parallel_tandem_08-02-01-3/";

    /*
     * XTandemBeanParameterTranslator translator = new XTandemBeanParameterTranslator(); XTandemJobProcessorFactoryImpl xTandemJobProcessorFactory = new XTandemJobProcessorFactoryImpl();
     * 
     * StagingDirectorySupplierImpl sdSupplier = new StagingDirectorySupplierImpl(); sdSupplier.setTempDirectoryPath("/project/xtandem"); xTandemJobProcessorFactory.setStagingDirectoryFactory(sdSupplier); xTandemJobProcessorFactory.setXslPath(xslPath);
     * xTandemJobProcessorFactory.setApplicationPath(xtandemPath); xTandemJobProcessorFactory.setParameterTranslator(translator); xTandemJobProcessorFactory.setType("xtandem"); xTandemJobProcessorFactory.setWorkingDirectory(workingDirectory);
     * xTandemJobProcessorFactory.setDisposableResourceTrackerSupplier(Suppliers.<DisposableResourceTracker>ofInstance(null));
     * 
     * XTandemJobProcessorImpl jobProcessor = xTandemJobProcessorFactory.get(); InputContext database = getInputContext("HUMAN.fasta"); InputContext mzxml = getInputContext("validMzXML.mzxml"); XTandemParameters parameters = new XTandemParameters();
     * ParameterUtils.setParametersFromProperties(ProteomicsTests.class.getResourceAsStream("xTandemInclusive.properties"),parameters);
     * 
     * jobProcessor.setInputMzXML(mzxml); jobProcessor.setDatabase(database); jobProcessor.setInputParameters(parameters); JobDescriptionType jobDescription = jobProcessor.preprocess();
     */

    final InputStream stream = JobQueueTest.class.getResourceAsStream("gramJobDescription.xml");
    final String jobDescriptionStr = IO_UTILS.toString(stream);
    final JobDescriptionType jobDescription = JobDescriptionUtils.deserialize(jobDescriptionStr);
    /*
     * TransferRequestType request = new TransferRequestType(); TransferType type = new TransferType(); type.setSourceUrl("file://home/elmo2/globus/A"); type.setDestinationUrl("file://home/elmo2/globus/B"); type.setAttempts(1); request.setTransfer(new TransferType[]{type});
     * jobDescription.setFileStageIn(request);
     */

    // GlobusCredential globusCredential = new GlobusCredential("/home/john/chiltoncred");
    final GlobusCredential globusCredential = new GlobusCredential("/home/john/chilton-test-1.msi.umn.edu-cert.pem", "/home/john/chilton-test-1.msi.umn.edu-key.pem");
    JobDescriptionUtils.setProxy(jobDescription, Credentials.get(globusCredential));
    // GSSCredential proxy = new GlobusGSSCredentialImpl(globusCredential, GSSCredential.INITIATE_AND_ACCEPT);
    // GlobusCredential globusCredential = GlobusCredential.getDefaultCredential();

    // String serviceAddress = "https://elmo.msi.umn.edu:8443/wsrf/services/ManagedJobFactoryService";
    final String serviceAddress = "https://haze.msi.umn.edu:8443/wsrf/services/ManagedJobFactoryService";

    final String factoryType = ManagedJobFactoryConstants.FACTORY_TYPE.PBS;
    final EndpointReferenceType factoryEndpoint = ManagedJobFactoryClientHelper.getFactoryEndpoint(serviceAddress, factoryType);

    // GridFtpClientFactoryImpl gridFtpClientFactory = new GridFtpClientFactoryImpl("haze.msi.umn.edu", 2811);
    // GridFtpClient gftpClient = gridFtpClientFactory.getGridFtpClient(globusCredential);
    // gftpClient.getOutputContext("/tmp/mootest").put("Moo Cow".getBytes());

    final GramJobFactoryGridImpl jobFactory = new GramJobFactoryGridImpl();
    final GramJob gramJob = jobFactory.getGramJob(jobDescription);
    gramJob.setCredentials(Credentials.get(globusCredential));

    final Integer xmlSecurity = Constants.SIGNATURE;
    gramJob.setMessageProtectionType(xmlSecurity);
    gramJob.setDelegationEnabled(true);
    final String submissionId = "uuid:" + UUIDGenFactory.getUUIDGen().nextUUID();
    gramJob.submit(factoryEndpoint, false, true, submissionId);

    /*
     * ManagedJobPortType jobPort = ManagedJobClientHelper.getPort(gramJob.getEndpoint()); GetResourcePropertyResponse response = jobPort.getResourceProperty(ManagedJobConstants.RP_STATE); SOAPElement[] any = response.get_any(); StateEnumeration state = (StateEnumeration)
     * ObjectDeserializer.toObject(any[0], StateEnumeration.class); System.out.println(state.toString());
     */

    /*
     * CreateManagedJobInputType jobInput = new CreateManagedJobInputType();
     * 
     * jobInput.setJobID(new AttributedURI("uuid:" + UUIDGenFactory.getUUIDGen().nextUUID())); Calendar cal = Calendar.getInstance(); cal.add(Calendar.MONTH, 1); jobInput.setInitialTerminationTime(cal);ed to acquire notification consumer home instance from registry [Caused by:
     * Name services is not bound in this Context] at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method) jobInput.setJob(jobDescription); CreateManagedJobOutputType createResponse = factoryPort.createManagedJob(jobInput); EndpointReferenceType jobEndpoint =
     * createResponse.getManagedJobEndpoint();
     * 
     * ManagedJobPortType jobPort = ManagedJobClientHelper.getPort(jobEndpoint);
     */

    /*
     * NotificationConsumerManager notifConsumerManager = NotificationConsumerManager.getInstance(); notifConsumerManager.startListening(); List topicPath = new LinkedList(); topicPath.add(ManagedJobConstants.RP_STATE); Util.registerTransport();
     */
    /*
     * job2.addListener(new GramJobListener() { public void stateChanged(GramJob arg0) { System.out.println("State update"); System.out.println(arg0.getState()); } });
     */
    // EndpointReferenceType jobEpr = ManagedJobClientHelper.getEndpoint(serviceAddress, submissionId);
    // System.out.println("ADDRESS: " + jobEpr.getAddress().toString() + "PARAMETESR " + jobEpr.getProperties());

    // GramExecutionStateResolverImpl resolver = new GramExecutionStateResolverImpl();

    while(true) {
      Thread.sleep(1000);
      System.out.println("State is " + gramJob.getState());
      // System.out.println("REAL JOB " + resolver.getgetState(gramJob.getHandle(), globusCredential));
      // System.out.println("FAKE JOB " + resolver.getState(gramJob.getHandle().replace("6", "8"), globusCredential));
      /*
       * ManagedJobPortType jobPort = ManagedJobClientHelper.getPort(gramJob.getEndpoint()); GetResourcePropertyResponse response = jobPort.getResourceProperty(ManagedJobConstants.RP_STATE); SOAPElement[] any = response.get_any(); StateEnumeration state = (StateEnumeration)
       * ObjectDeserializer.toObject(any[0], StateEnumeration.class); System.out.println(state.toString());
       */
      gramJob.refreshStatus();
      if(gramJob.getState().equals(StateEnumeration.Done)) {
        break;
      }
    }

    /*
    final File tempFile = TempFileSuppliers.getDefaultTempFileSupplier().get();
    try {
      // gftpClient.getInputContext("/tmp/dateoutput").get(tempFile);
      // System.out.println("Dateoutput" + FileUtilsFactory.getInstance().readFileToString(tempFile));
    } finally {
      tempFile.delete();
    }
    */
    // ManagedJobPortType jobPort = ManagedJobClientHelper.getPort(null);

  }

}
