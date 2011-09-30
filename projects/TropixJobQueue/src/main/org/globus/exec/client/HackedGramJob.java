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

package org.globus.exec.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.rpc.Stub;
import javax.xml.soap.SOAPElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.oasis.wsn.SubscriptionManager;
import org.oasis.wsn.TopicExpressionType;
import org.oasis.wsn.WSBaseNotificationServiceAddressingLocator;
import org.oasis.wsn.Subscribe;
import org.oasis.wsn.SubscribeResponse;
import org.oasis.wsrf.lifetime.ResourceUnknownFaultType;
import org.oasis.wsrf.lifetime.Destroy;
import org.oasis.wsrf.lifetime.SetTerminationTime;
import org.oasis.wsrf.lifetime.SetTerminationTimeResponse;
import org.oasis.wsrf.properties.GetMultipleResourceProperties_Element;
import org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse;
import org.oasis.wsrf.properties.GetResourcePropertyResponse;
import org.w3c.dom.Element;
import org.globus.axis.gsi.GSIConstants;
import org.globus.axis.util.Util;
import org.globus.delegation.DelegationConstants;
import org.globus.delegation.DelegationUtil;
import org.globus.delegationService.DelegationPortType;
import org.globus.delegationService.DelegationServiceAddressingLocator;
import org.globus.exec.generated.CreateManagedJobInputType;
import org.globus.exec.generated.CreateManagedJobOutputType;
import org.globus.exec.generated.FaultType;
import org.globus.exec.generated.FaultResourcePropertyType;
import org.globus.exec.generated.JobDescriptionType;
import org.globus.exec.generated.ManagedJobFactoryPortType;
import org.globus.exec.generated.ManagedJobPortType;
import org.globus.exec.generated.MultiJobDescriptionType;
import org.globus.exec.generated.ReleaseInputType;
import org.globus.exec.generated.ServiceLevelAgreementType;
import org.globus.exec.generated.StateChangeNotificationMessageType;
import org.globus.exec.generated.StateChangeNotificationMessageWrapperType;
import org.globus.exec.generated.StateEnumeration;
import org.globus.exec.utils.Resources;
import org.globus.exec.utils.ManagedExecutableJobConstants;
import org.globus.exec.utils.ManagedJobConstants;
import org.globus.exec.utils.ManagedJobFactoryConstants;
import org.globus.exec.utils.client.ManagedJobClientHelper;
import org.globus.exec.utils.client.ManagedJobFactoryClientHelper;
import org.globus.exec.utils.rsl.RSLHelper;
import org.globus.exec.utils.rsl.RSLParseException;
import org.globus.gram.GramException;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.gsi.jaas.JaasGssUtil;
import org.globus.rft.generated.DeleteRequestType;
import org.globus.rft.generated.TransferRequestType;
import org.globus.rft.generated.TransferType;
import org.globus.security.gridmap.GridMap;
import org.globus.util.I18n;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.NotificationConsumerManager;
import org.globus.wsrf.NotifyCallback;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.WSNConstants;
import org.globus.wsrf.container.ContainerException;
import org.globus.wsrf.container.ServiceContainer;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.impl.SimpleResourceKey;
import org.globus.wsrf.impl.security.authentication.Constants;
import org.globus.wsrf.impl.security.authorization.Authorization;
import org.globus.wsrf.impl.security.authorization.HostAuthorization;
import org.globus.wsrf.impl.security.authorization.IdentityAuthorization;
import org.globus.wsrf.impl.security.authorization.SelfAuthorization;
import org.globus.wsrf.impl.security.descriptor.ClientSecurityDescriptor;
import org.globus.wsrf.impl.security.descriptor.ContainerSecurityDescriptor;
import org.globus.wsrf.impl.security.descriptor.GSISecureMsgAuthMethod;
import org.globus.wsrf.impl.security.descriptor.GSISecureConvAuthMethod;
import org.globus.wsrf.impl.security.descriptor.GSITransportAuthMethod;
import org.globus.wsrf.impl.security.descriptor.ResourceSecurityDescriptor;
import org.globus.wsrf.impl.security.descriptor.SecurityDescriptorException;
import org.globus.wsrf.security.SecurityManager;
import org.globus.wsrf.utils.AddressingUtils;
import org.globus.wsrf.utils.AnyHelper;
import org.globus.wsrf.utils.XmlUtils;


/**
 * This is a slight variation on the class org.globus.exec.client.GramJob. This class provides all of the high level functionality of that class, but also exposes some added functionality, namely the ability to set both transport and message security. This functionality seems
 * necessary for interoperating with GRAM.NET.
 * 
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"DLS", "EI2", "ITA", "REC", "RV", "UPM", "BC"}, justification="Could fix the bugs, but better to mess with working globus code as little as possible.")
public class HackedGramJob implements NotifyCallback {
  private static Log logger = LogFactory.getLog(HackedGramJob.class.getName());
  private static I18n i18n = I18n.getI18n(Resources.class.getName());

  public static final int DEFAULT_DURATION_HOURS = 24;
  public static final int DEFAULT_TIMEOUT = 120000;
  public static final Integer DEFAULT_MSG_PROTECTION = Constants.SIGNATURE;
  public static final Authorization DEFAULT_AUTHZ = HostAuthorization.getInstance();
  private static final String BASE_SERVICE_PATH = "/wsrf/services/";
  private static final String SERVICE_PATH = BASE_SERVICE_PATH + "ManagedJobFactoryService";

  private static final String PERSONAL_SERVICE_PATH = BASE_SERVICE_PATH + "ManagedJobFactoryService";

  private Integer messageProtectionTypeOverride = null;
  private Integer transportProtectionTypOverride = null;

  private String securityType = null;
  private Integer msgProtectionType = DEFAULT_MSG_PROTECTION;
  private Authorization authorization = DEFAULT_AUTHZ;

  // holds job credentials
  private GSSCredential proxy = null;
  private boolean limitedDelegation = true;
  private boolean delegationEnabled = false;
  private boolean personal = false;
  private JobDescriptionType jobDescription;
  private EndpointReferenceType jobEndpointReference;
  private String jobHandle;
  private String id = null;

  // job status:
  private FaultType fault;
  private StateEnumeration state;
  private Object stateMonitor = new Object();
  private boolean holding;
  private int error;
  private int exitCode;
  private Vector listeners;

  private boolean destroyed = false;
  private Date duration;
  private Date terminationDate;
  private boolean useDefaultNotificationConsumer = true;
  private NotificationConsumerManager notificationConsumerManager;
  private EndpointReferenceType notificationConsumerEPR;
  private EndpointReferenceType notificationProducerEPR;
  private int axisStubTimeOut = DEFAULT_TIMEOUT;

  private EndpointReferenceType delegationFactoryEndpoint = null;
  private EndpointReferenceType stagingDelegationFactoryEndpoint = null;

  static {
    Util.registerTransport();
    // org.apache.xml.security.Init.init();
  }

  public Integer getMessageProtectionTypeOverride() {
    return messageProtectionTypeOverride;
  }

  public void setMessageProtectionTypeOverride(final Integer messageProtectionTypeOverride) {
    this.messageProtectionTypeOverride = messageProtectionTypeOverride;
  }

  public Integer getTransportProtectionTypOverride() {
    return transportProtectionTypOverride;
  }

  public void setTransportProtectionTypeOverride(final Integer transportProtectionTypOverride) {
    this.transportProtectionTypOverride = transportProtectionTypOverride;
  }

  /**
   * Creates a gram job with no RSL. This default constructor is used in conjunction with {@link #setEndpoint()}.
   */
  public HackedGramJob() {
    this.state = null;
    this.holding = false;
  }

  /**
   * Creates a gram job with specified job description.
   */
  public HackedGramJob(final JobDescriptionType jobDescription) {
    this();
    try {
      this.jobDescription = (JobDescriptionType) ObjectSerializer.clone(jobDescription);
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates a gram job with specified file containing the rsl.
   * 
   * Currently the rsl is required to be in the new XML-based form. For backwords compatibility this should accept the old format, but the conversion algorithm isn't in place yet.
   * 
   * @param rslFile
   *          file with job specification
   */
  public HackedGramJob(final File rslFile) throws RSLParseException, FileNotFoundException {
    // reading GT4 RSL
    this(RSLHelper.readRSL(rslFile));

  }

  /**
   * Creates a gram job with specified rsl.
   * 
   * Currently the rsl is required to be in the new XML-based form. For backwords compatibility this should accept the old format, but the conversion algorithm isn't in place yet.
   * 
   * @param rsl
   *          resource specification string
   */
  public HackedGramJob(final String rsl) throws RSLParseException {
    // reading GT4 RSL
    this(RSLHelper.readRSL(rsl));
  }

  /**
   * Add a listener to the GramJob. The listener will be notified whenever the state of the GramJob changes.
   * 
   * @param listener
   *          The object that wishes to receive state updates.
   * @see org.globus.gram.HackedGramJobListener
   */
  public void addListener(final HackedGramJobListener listener) {
    if(listeners == null) {
      listeners = new Vector();
    }
    listeners.addElement(listener);
  }

  /**
   * Remove a listener from the GramJob. The listener will no longer be notified of state changes for the GramJob.
   * 
   * @param listener
   *          The object that wishes to stop receiving state updates.
   * @see org.globus.gram.HackedGramJobListener
   */
  public void removeListener(final HackedGramJobListener listener) {
    if(listeners == null) {
      return;
    }
    listeners.removeElement(listener);
  }

  /**
   * Gets the credentials of this job.
   * 
   * @return job credentials. If null none were set.
   * 
   */
  public GSSCredential getCredentials() {
    return this.proxy;
  }

  /**
   * Sets credentials of the job.
   * 
   * @param newProxy
   *          user credentials
   * @throws IllegalArgumentException
   *           if credentials are already set
   */
  public void setCredentials(final GSSCredential newProxy) {
    if(this.proxy != null) {
      throw new IllegalArgumentException("Credentials already set");
    } else {
      this.proxy = newProxy;
    }
  }

  /**
   * Get the current state of this job.
   * 
   * @return current job state
   */
  public StateEnumeration getState() {
    return this.state;
  }

  /**
   * Sets the state of the job and update the local state listeners. Users should not call this function. <b>Precondition</b>state != null
   * 
   * @param state
   *          state of the job
   */
  private void setState(final StateEnumeration state, final boolean holding) {
    // ignore repeat or terminal -> non-terminal state notifications
    if(this.state != null && state.equals(this.state)) {
      return;
    }
    this.state = state;
    logger.debug("setting job state to " + state);

    this.holding = holding;
    logger.debug("holding: " + holding);

    if(listeners == null) {
      return;
    }
    int size = listeners.size();
    for(int i = 0; i < size; i++) {
      HackedGramJobListener listener = (HackedGramJobListener) listeners.elementAt(i);
      listener.stateChanged(this);
    }
  }

  public boolean isHolding() {
    return this.holding;
  }

  /**
   * Submits an interactive i.e. non-batch job with limited delegation
   * 
   * @see #request(String, String, boolean, boolean) for explanation of parameters
   */
  public void submit(final EndpointReferenceType factoryEndpoint) throws Exception {
    submit(factoryEndpoint, false, true, null);
  }

  /**
   * Submits a job with limited delegation.
   * 
   * @see #request(URL, String, boolean, boolean) for explanation of parameters
   */
  public void submit(final EndpointReferenceType factoryEndpoint, final boolean batch) throws Exception {
    submit(factoryEndpoint, batch, true, null);
  }

  /**
   * @todo add throws ...Exception for invalid credentials? Submits a job to the specified service either as an interactive or batch job. It can perform limited or full delegation.
   * 
   * @param factoryEndpoint
   *          the resource manager service endpoint. The service address can be specified in the following ways: <br>
   *          host <br>
   *          host:port <br>
   *          host:port/service <br>
   *          host/service <br>
   *          host:/service <br>
   *          host::subject <br>
   *          host:port:subject <br>
   *          host/service:subject <br>
   *          host:/service:subject <br>
   *          host:port/service:subject <br>
   * 
   * @param factoryEndpoint
   *          the endpoint reference to the job factory service
   * @param batch
   *          specifies if the job should be submitted as a batch job.
   * @param limitedDelegation
   *          true for limited delegation, false for full delegation.
   * @param jobId
   *          For reliable service instance creation, use the specified jobId to allow repeated, reliable attempts to submit the job submission in the presence of an unreliable transport.
   * 
   * @see #request(String) for detailed resource manager contact specification.
   */
  public void submit(final EndpointReferenceType factoryEndpointInput, final boolean batch, final boolean limitedDelegation, final String jobId) throws Exception {
    EndpointReferenceType factoryEndpoint = factoryEndpointInput;
    if(logger.isInfoEnabled()) {
      logger.info("<startTime name=\"submission\">" + System.currentTimeMillis() + "</startTime>");
    }

    this.id = jobId;
    this.limitedDelegation = limitedDelegation;

    EndpointReferenceType factoryEndpointOverride = this.jobDescription.getFactoryEndpoint();
    if(factoryEndpointOverride != null) {
      if(logger.isDebugEnabled()) {
        Element eprElement = ObjectSerializer.toElement(factoryEndpointOverride, RSLHelper.FACTORY_ENDPOINT_ATTRIBUTE_QNAME);
        logger.debug("Factory EPR Override: " + XmlUtils.toString(eprElement));
      }
      factoryEndpoint = factoryEndpointOverride;
    } else {
      if(logger.isDebugEnabled()) {
        logger.debug("No Factory Endpoint Override...using supplied.");
      }
      this.jobDescription.setFactoryEndpoint(factoryEndpoint);
    }

    if(logger.isDebugEnabled()) {
      Element eprElement = ObjectSerializer.toElement(factoryEndpoint, RSLHelper.FACTORY_ENDPOINT_ATTRIBUTE_QNAME);
      logger.debug("Factory EPR: " + XmlUtils.toString(eprElement));
    }

    if(factoryEndpoint != null) {
      setSecurityTypeFromEndpoint(factoryEndpoint);

      if(isDelegationEnabled()) {
        populateJobDescriptionEndpoints(factoryEndpoint);
      }
    }

    if(logger.isDebugEnabled()) {
      logger.debug("Job Submission ID: " + this.id);
    }

    ManagedJobFactoryPortType factoryPort = getManagedJobFactoryPortType(factoryEndpoint);
    this.jobEndpointReference = createJobEndpoint(factoryPort, batch);

    if(logger.isDebugEnabled()) {
      logger.debug("Job Endpoint:\n" + this.jobEndpointReference);
    }
  }

  private void setSecurityTypeFromEndpoint(final EndpointReferenceType epr) {
    if(logger.isDebugEnabled()) {
      logger.debug("Endpoint Address URL Scheme:\n" + epr.getAddress().getScheme());
    }

    if(this.securityType != null) {
      return;
    }

    if(epr.getAddress().getScheme().equals("http")) {
      if(logger.isDebugEnabled()) {
        // logger.debug("using message-level security");
        logger.debug("using secure conversation");
      }
      this.securityType = Constants.GSI_SEC_CONV;
    } else {
      if(logger.isDebugEnabled()) {
        logger.debug("using transport-level security");
      }
      this.securityType = Constants.GSI_TRANSPORT;
    }
  }

  private void populateJobDescriptionEndpoints(final EndpointReferenceType mjFactoryEndpoint) throws Exception {
    // get job delegation factory endpoints
    EndpointReferenceType[] delegationFactoryEndpoints = fetchDelegationFactoryEndpoints(mjFactoryEndpoint);

    // delegate to single/multi-job
    EndpointReferenceType delegationEndpoint = delegate(delegationFactoryEndpoints[0], this.limitedDelegation);
    this.jobDescription.setJobCredentialEndpoint(delegationEndpoint);
    // separate delegation not supported
    this.jobDescription.setStagingCredentialEndpoint(delegationEndpoint);
    if(logger.isDebugEnabled()) {
      logger.debug("delegated credential endpoint:\n" + delegationEndpoint);
    }
    // delegate to RFT
    populateStagingDescriptionEndpoints(mjFactoryEndpoint, delegationFactoryEndpoints[1], this.jobDescription);

    // delegate to sub-job if multi-job
    if(this.jobDescription instanceof MultiJobDescriptionType) {
      JobDescriptionType[] subJobDescriptions = ((MultiJobDescriptionType) this.jobDescription).getJob();
      for(int index = 0; index < subJobDescriptions.length; index++) {
        EndpointReferenceType subJobFactoryEndpoint = subJobDescriptions[index].getFactoryEndpoint();
        if(logger.isDebugEnabled()) {
          Element eprElement = ObjectSerializer.toElement(subJobFactoryEndpoint, RSLHelper.FACTORY_ENDPOINT_ATTRIBUTE_QNAME);
          logger.debug("Sub-Job Factory EPR: " + XmlUtils.toString(eprElement));
        }
        if(subJobFactoryEndpoint != null) {
          if(subJobFactoryEndpoint.getAddress() == null) {
            logger.error("Sub-Job Factory Endpoint Address is null.");
          }
          // get job delegation factory endpoints
          EndpointReferenceType[] subJobDelegationFactoryEndpoints = fetchDelegationFactoryEndpoints(subJobFactoryEndpoint);
          EndpointReferenceType subJobCredentialEndpoint = delegate(subJobDelegationFactoryEndpoints[0], true);
          subJobDescriptions[index].setJobCredentialEndpoint(subJobCredentialEndpoint);
          // separate delegation not supported
          subJobDescriptions[index].setStagingCredentialEndpoint(subJobCredentialEndpoint);
          if(logger.isDebugEnabled()) {
            logger.debug("sub-job delegated credential endpoint:\n" + subJobCredentialEndpoint);
          }
          // delegate to sub-job RFT
          populateStagingDescriptionEndpoints(subJobFactoryEndpoint, subJobDelegationFactoryEndpoints[1], subJobDescriptions[index]);
        }
      }
    }
  }

  private void populateStagingDescriptionEndpoints(final EndpointReferenceType mjFactoryEndpoint, final EndpointReferenceType delegationFactoryEndpoint, final JobDescriptionType jobDescription) throws Exception {
    // set staging factory endpoints and delegate
    TransferRequestType stageOut = jobDescription.getFileStageOut();
    TransferRequestType stageIn = jobDescription.getFileStageIn();
    DeleteRequestType cleanUp = jobDescription.getFileCleanUp();
    if((stageOut != null) || (stageIn != null) || (cleanUp != null)) {
      String factoryAddress = mjFactoryEndpoint.getAddress().toString();
      factoryAddress = factoryAddress.replaceFirst("ManagedJobFactoryService", "ReliableFileTransferFactoryService");

      // delegate to RFT
      EndpointReferenceType transferCredentialEndpoint = delegate(delegationFactoryEndpoint, true);

      if(logger.isDebugEnabled()) {
        logger.debug("transferCredentialEndpoint for job " + this.id + ":\n" + ObjectSerializer.toString(transferCredentialEndpoint, org.apache.axis.message.addressing.Constants.QNAME_ENDPOINT_REFERENCE));
      }

      // set delegated credential endpoint for stage-out
      if(stageOut != null) {
        stageOut.setTransferCredentialEndpoint(transferCredentialEndpoint);
      }

      // set delegated credential endpoint for stage-in
      if(stageIn != null) {
        stageIn.setTransferCredentialEndpoint(transferCredentialEndpoint);
      }

      // set delegated credential endpoint for clean up
      if(cleanUp != null) {
        cleanUp.setTransferCredentialEndpoint(transferCredentialEndpoint);
      }
    }
  }

  public EndpointReferenceType[] fetchDelegationFactoryEndpoints(final EndpointReferenceType factoryEndpoint) throws Exception {
    ManagedJobFactoryPortType factoryPort = getManagedJobFactoryPortType(factoryEndpoint);

    GetMultipleResourceProperties_Element request = new GetMultipleResourceProperties_Element();
    request.setResourceProperty(new QName[] {ManagedJobFactoryConstants.RP_DELEGATION_FACTORY_ENDPOINT, ManagedJobFactoryConstants.RP_STAGING_DELEGATION_FACTORY_ENDPOINT});
    if(logger.isInfoEnabled()) {
      logger.info("<startTime name=\"fetchDelegFactoryEndoints\">" + System.currentTimeMillis() + "</startTime>");
    }
    GetMultipleResourcePropertiesResponse response = factoryPort.getMultipleResourceProperties(request);
    if(logger.isInfoEnabled()) {
      logger.info("<endTime name=\"fetchDelegFactoryEndoints\">" + System.currentTimeMillis() + "</endTime>");
    }

    SOAPElement[] any = response.get_any();

    EndpointReferenceType[] endpoints = new EndpointReferenceType[] {(EndpointReferenceType) ObjectDeserializer.toObject(any[0], EndpointReferenceType.class), (EndpointReferenceType) ObjectDeserializer.toObject(any[1], EndpointReferenceType.class)};

    return endpoints;
  }

  private EndpointReferenceType delegate(final EndpointReferenceType delegationFactoryEndpoint, final boolean limitedDelegation) throws Exception {
    if(logger.isDebugEnabled()) {
      logger.debug("Delegation Factory Endpoint:\n" + delegationFactoryEndpoint);
    }

    // Credential to sign with
    GlobusCredential credential = null;
    if(this.proxy != null) {
      // user-specified credential
      credential = ((GlobusGSSCredentialImpl) this.proxy).getGlobusCredential();
    } else {
      // default credential
      credential = GlobusCredential.getDefaultCredential();
    }

    // lifetime in seconds
    int lifetime = DEFAULT_DURATION_HOURS * 60 * 60;
    if(this.duration != null) {
      long currentTime = System.currentTimeMillis();
      lifetime = (int) (this.duration.getTime() - currentTime);
    }

    ClientSecurityDescriptor secDesc = new ClientSecurityDescriptor();
    if(this.securityType.equals(Constants.GSI_SEC_MSG)) {
      if(logger.isDebugEnabled()) {
        logger.debug("Setting GSISecureMsg protection type");
      }
      secDesc.setGSISecureMsg(this.getMessageProtectionType());
    } else if(this.securityType.equals(Constants.GSI_SEC_CONV)) {
      if(logger.isDebugEnabled()) {
        logger.debug("Setting GSISecureConv protection type");
      }
      secDesc.setGSISecureConv(this.getMessageProtectionType());
    } else {
      if(logger.isDebugEnabled()) {
        logger.debug("Setting GSITransport protection type");
      }
      secDesc.setGSITransport(this.getMessageProtectionType());
    }
    secDesc.setAuthz(getAuthorization());

    if(this.proxy != null) {
      secDesc.setGSSCredential(this.proxy);
    }
    // Get the public key to delegate on.
    if(logger.isInfoEnabled()) {
      logger.info("<startTime name=\"fetchDelegCertChainRP\">" + System.currentTimeMillis() + "</startTime>");
    }
    X509Certificate[] certsToDelegateOn = DelegationUtil.getCertificateChainRP(delegationFactoryEndpoint, secDesc);
    if(logger.isInfoEnabled()) {
      logger.info("<endTime name=\"fetchDelegCertChainRP\">" + System.currentTimeMillis() + "</endTime>");
    }
    X509Certificate certToSign = certsToDelegateOn[0];

    if(logger.isDebugEnabled()) {
      logger.debug("delegating...using authz method " + getAuthorization());
    }

    // FIXME remove when there is a DelegationUtil.delegate(EPR, ...)
    String protocol = delegationFactoryEndpoint.getAddress().getScheme();
    String host = delegationFactoryEndpoint.getAddress().getHost();
    int port = delegationFactoryEndpoint.getAddress().getPort();
    String factoryUrl = protocol + "://" + host + ":" + port + BASE_SERVICE_PATH + DelegationConstants.FACTORY_PATH;
    if(logger.isDebugEnabled()) {
      logger.debug("Delegation Factory URL " + factoryUrl);
    }

    // send to delegation service and get epr.
    if(logger.isInfoEnabled()) {
      logger.info("<startTime name=\"delegate\">" + System.currentTimeMillis() + "</startTime>");
    }
    EndpointReferenceType credentialEndpoint = DelegationUtil.delegate(factoryUrl, credential, certToSign, lifetime, !limitedDelegation, secDesc);
    // getAuthorization());
    if(logger.isDebugEnabled()) {
      logger.debug("Delegated Credential Endpoint:\n" + credentialEndpoint);
    }
    if(logger.isInfoEnabled()) {
      logger.info("<endTime name=\"delegate\">" + System.currentTimeMillis() + "</endTime>");
    }

    return credentialEndpoint;
  }

  /**
   * 
   * @param pathArray
   *          String[] old
   * @param newPath
   *          String
   * @return String[] new path array
   */
  private String[] addPathToArray(final String[] pathArray, final String newPath) {
    String[] newPathArray;
    if(pathArray != null) {
      List newPathList = new ArrayList(Arrays.asList(pathArray));
      newPathList.add(newPath);
      newPathArray = (String[]) newPathList.toArray(new String[0]);
    } else {
      newPathArray = new String[1];
      newPathArray[0] = newPath;
    }
    return newPathArray;

  }

  private String catenate(final String iBaseURL, final String path) {
    String baseURL = iBaseURL;
    final String sep = "/";
    String newPath = path;
    if(path.indexOf("://") < 0) { // not a URL already
      if(baseURL.endsWith(sep)) {
        baseURL = baseURL.substring(0, baseURL.length() - 1);
      }
      // assert !baseURL.endsWith(SEPARATOR)
      if(!path.startsWith(sep)) {
        baseURL = baseURL + sep;
      }
      newPath = baseURL + path;
    } else {
      if(logger.isDebugEnabled()) {
        logger.debug("path " + path + " is a URL already. No prepending of URL");
      }
    }
    return newPath;
  }

  public void prependBaseURLtoStageInSources(final String baseURL) {
    TransferRequestType stageInDirectives = this.jobDescription.getFileStageIn();
    if(stageInDirectives != null) {
      TransferType[] transferArray = stageInDirectives.getTransfer();
      for(int i = 0; i < transferArray.length; i++) {
        String source = transferArray[i].getSourceUrl();
        transferArray[i].setSourceUrl(catenate(baseURL, source));
      }
    } else {
      logger.debug("no stage in directives");
    }
  }

  public void prependBaseURLtoStageOutDestinations(final String baseURL) {
    TransferRequestType stageOutDirectives = this.jobDescription.getFileStageOut();
    if(stageOutDirectives != null) {
      TransferType[] transferArray = stageOutDirectives.getTransfer();
      for(int i = 0; i < transferArray.length; i++) {
        String source = transferArray[i].getDestinationUrl();
        transferArray[i].setDestinationUrl(catenate(baseURL, source));
      }
    } else {
      logger.debug("no stage out directives");
    }
  }

  /**
   * <b>Precondition</b>the job has not been submitted
   * 
   * @param path
   *          String
   */
  /*
   * public void setDryRun(boolean enabled) { this.jobDescription.setDryRun(new Boolean(enabled)); }
   */

  /**
   * @return the job description
   */
  public JobDescriptionType getDescription() throws Exception {
    if(this.jobDescription == null) {
      refreshRSLAttributes();
    }
    return this.jobDescription;
  }

  private EndpointReferenceType createJobEndpoint(final ManagedJobFactoryPortType factoryPort, final boolean batch) throws Exception {
    // Create a service instance base on creation info
    logger.debug("creating ManagedJob instance");

    if(logger.isDebugEnabled()) {
      long millis = System.currentTimeMillis();
      BigDecimal seconds = new BigDecimal(((double) millis) / 1000);
      seconds = seconds.setScale(3, BigDecimal.ROUND_HALF_DOWN);
      logger.debug("submission time, in seconds from the Epoch:" + "\nbefore: " + seconds.toString());
      logger.debug("\nbefore, in milliseconds: " + millis);
    }

    ((org.apache.axis.client.Stub) factoryPort).setTimeout(this.axisStubTimeOut);

    CreateManagedJobInputType jobInput = new CreateManagedJobInputType();
    jobInput.setInitialTerminationTime(getTerminationTime());
    if(this.id != null) {
      jobInput.setJobID(new AttributedURI(this.id));
    }
    if(this.jobDescription instanceof MultiJobDescriptionType) {
      jobInput.setMultiJob((MultiJobDescriptionType) this.getDescription());
    } else {
      jobInput.setJob(this.getDescription());
    }

    if(!batch) {
      if(this.useDefaultNotificationConsumer) {
        setupNotificationConsumerManager();
      }

      try {
        if(this.useDefaultNotificationConsumer) {
          setupNotificationConsumer();
        }

        Subscribe subscriptionRequest = new Subscribe();
        subscriptionRequest.setConsumerReference(this.notificationConsumerEPR);
        TopicExpressionType topicExpression = new TopicExpressionType(WSNConstants.SIMPLE_TOPIC_DIALECT, ManagedJobConstants.RP_STATE);
        subscriptionRequest.setTopicExpression(topicExpression);
        jobInput.setSubscribe(subscriptionRequest);
      } catch(Exception e) {
        // may happen...? Let's not fail.
        logger.error(e);
        try {
          unbind();
        } catch(Exception unbindEx) {
          // let's not fail the unbinding
          logger.error(unbindEx);
        }
      }
    }

    if(logger.isInfoEnabled()) {
      logger.info("<startTime name=\"createManagedJob\">" + System.currentTimeMillis() + "</startTime>");
    }
    CreateManagedJobOutputType response = factoryPort.createManagedJob(jobInput);
    if(logger.isInfoEnabled()) {
      logger.info("<endTime name=\"createManagedJob\">" + System.currentTimeMillis() + "</endTime");
    }
    EndpointReferenceType jobEPR = (EndpointReferenceType) ObjectSerializer.clone(response.getManagedJobEndpoint());

    // This doesn't work with GRAM.NET for some reason...
    /*
    if(logger.isDebugEnabled()) {
       * logger.debug("Job Handle: " + AuditUtil.eprToGridId(jobEPR)); Element jobEndpointElement = null; try { jobEndpointElement = ObjectSerializer.toElement( jobEPR, new QName( "http://schemas.xmlsoap.org/ws/2004/03/addressing", "EndpointReferenceType")); } catch (Exception e)
       * { throw new RuntimeException(e); } logger.debug("Job EPR: " + XmlUtils.toString(jobEndpointElement));
    }
       */

    EndpointReferenceType subscriptionEPR = response.getSubscriptionEndpoint();

    if(subscriptionEPR != null) {
      this.notificationProducerEPR = (EndpointReferenceType) ObjectSerializer.clone(subscriptionEPR);
    }

    if(logger.isDebugEnabled()) {
      Calendar terminationTime = response.getNewTerminationTime();
      Calendar serviceCurrentTime = response.getCurrentTime();
      logger.debug("Termination time granted by the factory to the job resource: " + terminationTime.getTime());
      logger.debug("Current time seen by the factory service on creation: " + serviceCurrentTime.getTime());
    }

    return jobEPR;
  }

  private void setupNotificationConsumerManager() throws GSSException, ContainerException {
    logger.debug("Security Type: " + this.securityType);

    if(this.securityType.equals(Constants.GSI_SEC_MSG) || this.securityType.equals(Constants.GSI_SEC_CONV)) {
      logger.debug("Setting up a non-secure consumer manager.");

      // start an embedded container with a notification consumer
      this.notificationConsumerManager = NotificationConsumerManager.getInstance();
    } else {
      logger.debug("Setting up a secure consumer manager.");

      // embedded container properties
      Map properties = new HashMap();

      // make sure the embedded container speaks GSI Transport Security
      properties.put(ServiceContainer.CLASS, "org.globus.wsrf.container.GSIServiceContainer");

      // specify the credentials to use for the embedded container
      if(this.proxy != null) {
        // user-specified credential
        ContainerSecurityDescriptor containerSecDesc = new ContainerSecurityDescriptor();
        SecurityManager secManager = SecurityManager.getManager();
        containerSecDesc.setSubject(JaasGssUtil.createSubject(this.proxy));
        properties.put(ServiceContainer.CONTAINER_DESCRIPTOR, containerSecDesc);
      }

      // start a secure embedded container with a notif. consumer
      this.notificationConsumerManager = NotificationConsumerManager.getInstance(properties);
    }

    this.notificationConsumerManager.startListening();
  }

  private void setupNotificationConsumer() throws SecurityDescriptorException, ResourceException {
    logger.debug("Setting up notification consumer.");

    List topicPath = new LinkedList();
    topicPath.add(ManagedJobConstants.RP_STATE);

    ResourceSecurityDescriptor securityDescriptor = new ResourceSecurityDescriptor();
    // TODO implement "service-side host authorization"
    String authz = null;
    if(authorization == null) {
      authz = Authorization.AUTHZ_NONE;
    } else if(authorization instanceof HostAuthorization) {
      authz = Authorization.AUTHZ_NONE;
    } else if(authorization instanceof SelfAuthorization) {
      authz = Authorization.AUTHZ_SELF;
    } else if(authorization instanceof IdentityAuthorization) {
      GridMap gridMap = new GridMap();
      gridMap.map(((IdentityAuthorization) authorization).getIdentity(), "HaCk");
      securityDescriptor.setGridMap(gridMap);

      authz = Authorization.AUTHZ_GRIDMAP;
    } else {
      logger.error("Unsupported authorization method class " + authorization.getClass().getName());
      return;
    }
    securityDescriptor.setAuthz(authz);
    Vector authMethod = new Vector();
    logger.debug("Security Type: " + this.securityType);
    if(this.securityType.equals(Constants.GSI_SEC_MSG)) {
      authMethod.add(GSISecureMsgAuthMethod.BOTH);
    } else if(this.securityType.equals(Constants.GSI_SEC_CONV)) {
      authMethod.add(GSISecureConvAuthMethod.BOTH);
    } else {
      authMethod.add(GSITransportAuthMethod.BOTH);
    }
    securityDescriptor.setAuthMethods(authMethod);

    this.notificationConsumerEPR = this.notificationConsumerManager.createNotificationConsumer(topicPath, this, securityDescriptor);

    if(logger.isDebugEnabled()) {
      logger.debug("notification consumer endpoint:\n" + this.notificationConsumerEPR);
    }
  }

  /**
   * Returns true if the job has been requested. Useful to determine if destroy() can be called when it is not obvious.
   */
  public boolean isRequested() {
    // TODO see if can replace with check on state (== ACTIVE?)
    return this.jobEndpointReference != null;
  }

  public void setPersonal(final boolean personal) {
    this.personal = personal;
  }

  public boolean isPersonal() {
    return this.personal;
  }

  private ManagedJobFactoryPortType getManagedJobFactoryPortType(final EndpointReferenceType factoryEndpoint) throws Exception {
    ManagedJobFactoryPortType factoryPort = ManagedJobFactoryClientHelper.getPort(factoryEndpoint);

    setStubSecurityProperties((Stub) factoryPort);

    return factoryPort;
  }

  /**
   * Cancels a job.
   */
  public void cancel() throws Exception {
    logger.debug("destroy() called in cancel()");
    destroy();
    setState(StateEnumeration.Failed, false);
  }

  /**
   * Registers a callback listener for this job. (Reconnects to the job) <b>Precondition</b> this.jobEndpointReference != null
   * 
   * @throws GramException
   *           if error occurs during job registration.
   * @throws GSSException
   *           if user credentials are invalid.
   */
  public void bind() throws GramException, Exception {
    logger.debug("bind() called");
    if(this.useDefaultNotificationConsumer) {
      setupNotificationConsumerManager();
    }

    // consumer started listening --> unbind() may be called to recover
    try {
      if(this.useDefaultNotificationConsumer) {
        setupNotificationConsumer();
      }

      Subscribe request = new Subscribe();
      request.setConsumerReference(this.notificationConsumerEPR);
      TopicExpressionType topicExpression = new TopicExpressionType(WSNConstants.SIMPLE_TOPIC_DIALECT, ManagedJobConstants.RP_STATE);
      request.setTopicExpression(topicExpression);

      ManagedJobPortType jobPort = ManagedJobClientHelper.getPort(this.jobEndpointReference);
      setStubSecurityProperties((Stub) jobPort);

      SubscribeResponse response = jobPort.subscribe(request);
      this.notificationProducerEPR = (EndpointReferenceType) ObjectSerializer.clone(response.getSubscriptionReference());

      if(logger.isDebugEnabled()) {
        logger.debug("notification producer endpoint:\n" + this.notificationProducerEPR);
      }
    } catch(Exception e) {
      // may happen...? Let's not fail.
      logger.error(e);
      try {
        unbind();
      } catch(Exception unbindEx) {
        // let's not fail the unbinding
        logger.error(unbindEx);
      }
    }
  }

  /**
   * Unregisters a callback listener for this job. (disconnects from the job) <b>Precondition</b> ClientNotificationConsumer.isListening()
   */
  public void unbind() throws NoSuchResourceException, Exception {
    // unsubscribe
    if(this.notificationProducerEPR != null) {
      SubscriptionManager subscriptionPort = new WSBaseNotificationServiceAddressingLocator().getSubscriptionManagerPort(this.notificationProducerEPR);

      setStubSecurityProperties((Stub) subscriptionPort);

      if(logger.isInfoEnabled()) {
        logger.info("<startTime name=\"subscriptionDestroy\">" + System.currentTimeMillis() + "</startTime>");
      }
      if(logger.isDebugEnabled()) {
        logger.debug("Calling destroy on notificationProducerEPR:\n" + ObjectSerializer.toString(this.notificationProducerEPR, org.apache.axis.message.addressing.Constants.QNAME_ENDPOINT_REFERENCE));
      }
      subscriptionPort.destroy(new Destroy());
      if(logger.isInfoEnabled()) {
        logger.info("<endTime name=\"subscriptionDestroy\">" + System.currentTimeMillis() + "</endTime>");
      }
    }

    // stop notification consumer
    if(!this.notificationConsumerManager.isListening()) {
      String errorMessage = i18n.getMessage(Resources.PRECONDITION_VIOLATION, "!notificationConsumerManager.isListening()");
      throw new RuntimeException(errorMessage);
    }

    if(this.notificationConsumerEPR != null) {
      logger.debug("removing the notification consumer");
      this.notificationConsumerManager.removeNotificationConsumer(notificationConsumerEPR);
    }
    logger.debug("stopping the consumer manager from listening");
    this.notificationConsumerManager.stopListening();
  }

  /**
   * Precondition: isRequested() Postcondition: isLocallyDestroyed().
   * 
   * @throws GramException
   *           if error occurs during job service destruction.
   */
  public synchronized void destroy() throws Exception {
    if(!this.destroyed) {
      logger.debug("destroy() called");

      // fetch the job description needed for cleaning up the delegated
      // credential if this is a batch job
      if(isDelegationEnabled()) {
        if(logger.isDebugEnabled()) {
          logger.debug("Job Description for job " + this.id + " (BEFORE):\n" + toString());
        }

        getDescription();

        if(logger.isDebugEnabled()) {
          logger.debug("Job Description for job " + this.id + " (AFTER):\n" + toString());
        }
      }

      try {
        if((this.notificationConsumerManager != null) && this.notificationConsumerManager.isListening()) {
          unbind();
        }
      } catch(NoSuchResourceException noSuchResEx) {
        String warnMessage = i18n.getMessage(Resources.REMOTE_RESOURCE_DESTROY_ERROR);
        logger.warn(warnMessage, noSuchResEx);
        // not an error - the job may have
        // been automatically destroyed by soft state
      }
      ManagedJobPortType jobPort = ManagedJobClientHelper.getPort(this.jobEndpointReference);
      setStubSecurityProperties((Stub) jobPort);
      if(logger.isInfoEnabled()) {
        logger.info("<startTime name=\"destroy\">" + System.currentTimeMillis() + "</startTime>");
      }
      try {
        if(logger.isDebugEnabled()) {
          logger.debug("Calling destroy on jobEndpointReference:\n" + ObjectSerializer.toString(this.jobEndpointReference, org.apache.axis.message.addressing.Constants.QNAME_ENDPOINT_REFERENCE));
        }

        jobPort.destroy(new Destroy());
      } catch(ResourceUnknownFaultType resUnknownFault) {
        String warnMessage = i18n.getMessage(Resources.REMOTE_RESOURCE_DESTROY_ERROR);
        logger.warn(warnMessage, resUnknownFault);
        // not an error - the job may have
        // been automatically destroyed by soft state
      }
      if(logger.isInfoEnabled()) {
        logger.info("<endTime name=\"destroy\">" + System.currentTimeMillis() + "</endTime>");
      }

      if(isDelegationEnabled()) {
        destroyDelegatedCredentials();
      }

      this.destroyed = true;

    } else {
      logger.warn("destroy() already called");
      // Do Nothing here
    }
  }

  private void destroyDelegatedCredentials() throws Exception {
    // destroy the job credential
    EndpointReferenceType jobCredentialEndpoint = this.jobDescription.getJobCredentialEndpoint();
    if(jobCredentialEndpoint != null) {
      if(logger.isDebugEnabled()) {
        logger.debug("Calling destroy on jobCredentialEndpoint:\n" + ObjectSerializer.toString(jobCredentialEndpoint, org.apache.axis.message.addressing.Constants.QNAME_ENDPOINT_REFERENCE));
      }

      destroyDelegatedCredential(jobCredentialEndpoint);
    }

    // not destroying staging credential because this client sets it
    // to the same value as the job credential

    destroyTransferDelegatedCredential(this.jobDescription);

    // destroy sub-job delegated credentials if multi-job
    if(this.jobDescription instanceof MultiJobDescriptionType) {
      JobDescriptionType[] subJobDescriptions = ((MultiJobDescriptionType) this.jobDescription).getJob();
      for(int index = 0; index < subJobDescriptions.length; index++) {
        EndpointReferenceType subJobCredentialEndpoint = subJobDescriptions[index].getJobCredentialEndpoint();
        if(subJobCredentialEndpoint != null) {
          destroyDelegatedCredential(subJobCredentialEndpoint);
        }

        destroyTransferDelegatedCredential(subJobDescriptions[index]);

      }
    }
  }

  private void destroyTransferDelegatedCredential(final JobDescriptionType jobDescription) throws Exception {
    // set staging factory endpoints and delegate
    TransferRequestType stageOut = jobDescription.getFileStageOut();
    TransferRequestType stageIn = jobDescription.getFileStageIn();
    DeleteRequestType cleanUp = jobDescription.getFileCleanUp();
    EndpointReferenceType transferCredentialEndpoint = null;
    if(stageOut != null) {
      transferCredentialEndpoint = stageOut.getTransferCredentialEndpoint();
    } else if(stageIn != null) {
      transferCredentialEndpoint = stageIn.getTransferCredentialEndpoint();
    } else if(cleanUp != null) {
      transferCredentialEndpoint = cleanUp.getTransferCredentialEndpoint();
    }

    if(transferCredentialEndpoint != null) {
      if(logger.isDebugEnabled()) {
        logger.debug("Calling destroy on transferCredentialEndpoint for" + " job " + this.id + ":\n" + ObjectSerializer.toString(transferCredentialEndpoint, org.apache.axis.message.addressing.Constants.QNAME_ENDPOINT_REFERENCE));
      }

      destroyDelegatedCredential(transferCredentialEndpoint);
    }
  }

  private void destroyDelegatedCredential(final EndpointReferenceType credentialEndpoint) throws Exception {
    DelegationPortType delegatedCredentialPort = new DelegationServiceAddressingLocator().getDelegationPortTypePort(credentialEndpoint);
    setStubSecurityProperties((Stub) delegatedCredentialPort);
    try {
      if(logger.isInfoEnabled()) {
        logger.info("<startTime name=\"delegatedCredentialDestroy\">" + System.currentTimeMillis() + "</startTime>");
      }
      delegatedCredentialPort.destroy(new Destroy());
      if(logger.isInfoEnabled()) {
        logger.info("<endTime name=\"delegatedCredentialDestroy\">" + System.currentTimeMillis() + "</endTime>");
      }
    } catch(ResourceUnknownFaultType resUnknownFault) {
      logger.warn("Unable to destroy resource");
      if(logger.isDebugEnabled()) {
        resUnknownFault.printStackTrace();
      }
      // not an error - the job may have
      // been automatically destroyed by soft state
    }
  }

  /**
   * 
   * @return boolean true if #destroy() destroy() has been called
   */
  public synchronized boolean isLocallyDestroyed() {
    return this.destroyed;
  }

  public void release() throws Exception {

    ManagedJobPortType jobPort = ManagedJobClientHelper.getPort(this.jobEndpointReference);

    setStubSecurityProperties((Stub) jobPort);

    org.apache.axis.client.Stub s = (org.apache.axis.client.Stub) jobPort;
    s.setTimeout(this.axisStubTimeOut);
    logger.debug("setting timeout for Axis to " + this.axisStubTimeOut + " ms");

    logger.debug("releasing ManagedJob from hold");
    jobPort.release(new ReleaseInputType());
  }

  /**
   * Sets the error code of the job. Note: User should not use this method.
   * 
   * @param code
   *          error code
   */
  protected void setError(final int code) {
    //this.error = error;
  }

  /**
   * Gets the error of the job.
   * 
   * @return error number of the job.
   */
  public int getError() {
    return error;
  }

  /**
   * Return information about the cause of a job failure (when <code>getStateAsString.equals(StateEnumeration._Failed)</code>).
   */
  public FaultType getFault() {
    return this.fault;
  }

  /**
   * <b>Precondition</b>: isRequested().
   */
  public EndpointReferenceType getEndpoint() {
    return this.jobEndpointReference;
  }

  public void setEndpoint(final EndpointReferenceType endpoint) throws Exception {
    this.jobEndpointReference = endpoint;
    if(this.jobEndpointReference != null) {
      setSecurityTypeFromEndpoint(this.jobEndpointReference);
    }
  }

  public String getID() {
    return this.id;
  }

  /**
   * Return string representing the endpoint to the job. The service URL and the resource ID are extracted from the endpoint reference in order to create the handle. Any other data in the EPR such as other reference properties, parameters or policies is lost in the "conversion".
   * An EPR can be created out of the handle by using the getEndpoint(String) function.
   * 
   * @param endpoint
   *          EndpointReferenceType the endpoint to convert
   * @return String the handle, of format handle :== <serviceURL>HANDLE_SEPARATOR<resourceID>
   */
  private static String getHandle(final EndpointReferenceType endpoint) {

    String serviceAddress = endpoint.getAddress().toString();
    MessageElement referenceProperty = endpoint.getProperties().get(ManagedJobConstants.RESOURCE_KEY_QNAME);
    String resourceID;
    if(referenceProperty != null) {
      resourceID = referenceProperty.getValue(); // key is a string
    } else {
      MessageElement dotNetProperty = endpoint.getProperties().get(new QName("http://gcg.cs.virginia.edu/wsrf", "resourceID"));
      resourceID = "DOTNET" + dotNetProperty.getValue();
    }
    return serviceAddress + HANDLE_SEPARATOR + resourceID;
  }

  private static EndpointReferenceType getEndpoint(final String handle) throws Exception {
    int resourceIdStart = handle.indexOf(HANDLE_SEPARATOR) + 1;
    String resourceID = handle.substring(resourceIdStart);
    String serviceAddress = handle.substring(0, resourceIdStart - 1);
    ResourceKey key;
    if(resourceID.startsWith("DOTNET")) {
      key = new SimpleResourceKey(new QName("http://gcg.cs.virginia.edu/wsrf", "resourceID"), resourceID.substring("DOTNET".length()));
    } else {
      key = getResourceKey(resourceID);
    }
    return AddressingUtils.createEndpointReference(serviceAddress, key);
  }

  private static final String HANDLE_SEPARATOR = "?";

  /**
   * This function is public so that consumers can use it to generate the key object to a resource. There is no need for a specific ResourceKey class as the actual key value is just a string.
   */
  private static SimpleResourceKey getResourceKey(final String keyValue) {
    return new SimpleResourceKey(ManagedJobConstants.RESOURCE_KEY_QNAME, keyValue);
  }

  /**
   * Can be used instead of {@link #getEndpointReference} <b>Precondition</b>: isRequested().
   */
  public String getHandle() {
    if(this.jobHandle == null) {
      if(logger.isDebugEnabled()) {
        logger.debug("Generating handle from endpoint " + this.jobEndpointReference);
      }
      this.jobHandle = getHandle(this.jobEndpointReference); // ResolverUtils.getResourceHandle(
      if(logger.isDebugEnabled()) {
        logger.debug("New handle: " + this.jobHandle);
      }
    }
    return this.jobHandle;
  }

  /**
   * Can be used instead of {@link #setEndpointReference}.
   */
  public void setHandle(final String handle) throws Exception {
    this.jobHandle = handle;
    if(this.jobHandle != null) {
      this.jobEndpointReference = getEndpoint(handle);
      setSecurityTypeFromEndpoint(this.jobEndpointReference);
    }
  }

  public int getExitCode() {
    return exitCode;
  }

  /**
   * Set timeout for HTTP socket. Default is 120000 (2 minutes).
   * 
   * @param timeout
   *          the timeout value, in milliseconds.
   */
  public void setTimeOut(final int timeout) {
    this.axisStubTimeOut = timeout;
  }

  /**
   * Returns string representation of this job.
   * 
   * @return string representation of this job. Useful for debugging.
   */
  public String toString() {
    String jobDescString = "RSL: ";
    JobDescriptionType jobDesc = null;
    try {
      jobDesc = this.getDescription();
    } catch(Exception e) {
      String errorMessage = i18n.getMessage(Resources.FETCH_JOB_DESCRIPTION_ERROR);
      logger.error(errorMessage, e);
    }
    if(jobDesc != null) {
      jobDescString += RSLHelper.convertToString(jobDesc);
    }
    return jobDescString;
    /**
     * @todo print ID of job (resource key?)
     */
  }

  /**
   * Deliver the notification message.
   * 
   * @param topicPath
   *          The topic path for the topic that generated the notification.
   * @param producer
   *          The producer endpoint reference.
   * @param message
   *          The notification message.
   */
  public void deliver(final List topicPath, final EndpointReferenceType producer, final Object message) {
    if(logger.isDebugEnabled()) {
      logger.debug("receiving notification");
      if(message instanceof Element) {
        logger.debug("message is of type " + message.getClass().getName());
        logger.debug("message contents: \n" + XmlUtils.toString((Element) message));
      }
    }

    try {
      StateChangeNotificationMessageType changeNotification = null;
      if(message instanceof StateChangeNotificationMessageWrapperType) {
        changeNotification = ((StateChangeNotificationMessageWrapperType) message).getStateChangeNotificationMessage();
      } else {
        changeNotification = (StateChangeNotificationMessageType) ObjectDeserializer.toObject((Element) message, StateChangeNotificationMessageType.class);
      }
      StateEnumeration state = changeNotification.getState();
      boolean holding = changeNotification.isHolding();
      if(state.equals(StateEnumeration.Failed)) {
        setFault(getFaultFromRP(changeNotification.getFault()));
      }
      if(state.equals(StateEnumeration.StageOut) || state.equals(StateEnumeration.Done) || state.equals(StateEnumeration.Failed)) {
        this.exitCode = changeNotification.getExitCode();

        if(logger.isDebugEnabled()) {
          logger.debug("Setting exit code to " + Integer.toString(exitCode));
        }
        if(logger.isInfoEnabled()) {
          logger.info("<endTime name=\"submission\">" + System.currentTimeMillis() + "</endTime>");
        }
      }

      synchronized(this.stateMonitor) {
        if((this.notificationConsumerManager != null) && !this.notificationConsumerManager.isListening()) {
          return;
        }

        setState(state, holding);
      }
    } catch(Exception e) {
      String errorMessage = "Notification message processing failed: " + "Could not get value or set new status.";
      logger.error(errorMessage, e);
      // no propagation of error here?
    }
  }

  private void setFault(final FaultType fault) throws Exception {
    this.fault = fault;
  }

  private FaultType getFaultFromRP(final FaultResourcePropertyType fault) {
    if(fault == null) {
      return null;
    }

    if(fault.getFault() != null) {
      return fault.getFault();
    } else if(fault.getCredentialSerializationFault() != null) {
      return fault.getCredentialSerializationFault();
    } else if(fault.getExecutionFailedFault() != null) {
      return fault.getExecutionFailedFault();
    } else if(fault.getFilePermissionsFault() != null) {
      return fault.getFilePermissionsFault();
    } else if(fault.getInsufficientCredentialsFault() != null) {
      return fault.getInsufficientCredentialsFault();
    } else if(fault.getInternalFault() != null) {
      return fault.getInternalFault();
    } else if(fault.getInvalidCredentialsFault() != null) {
      return fault.getInvalidCredentialsFault();
    } else if(fault.getInvalidPathFault() != null) {
      return fault.getInvalidPathFault();
    } else if(fault.getServiceLevelAgreementFault() != null) {
      return fault.getServiceLevelAgreementFault();
    } else if(fault.getStagingFault() != null) {
      return fault.getStagingFault();
    } else if(fault.getUnsupportedFeatureFault() != null) {
      return fault.getUnsupportedFeatureFault();
    } else {
      return null;
    }
  }

  private FaultType deserializeFaultRP(final SOAPElement any) throws DeserializationException {
    return getFaultFromRP((FaultResourcePropertyType) ObjectDeserializer.toObject(any, FaultResourcePropertyType.class));
  }

  /**
   * Asks the job service for its state,i.e. its state and the cause if the state is 'Failed'. This is useful when subscribing to notifications is impossible but an immediate result is needed. <b>Precondition</b>job has been submitted
   * 
   * @throws Exception
   *           if the service data cannot be fetched or the job state not extracted from the data.
   */
  public void refreshStatus() throws Exception {
    if(logger.isDebugEnabled()) {
      logger.debug("refreshing state of job with endpoint " + this.jobEndpointReference);
    }

    boolean singleJob = isSingleJob();

    ManagedJobPortType jobPort = ManagedJobClientHelper.getPort(this.jobEndpointReference);

    setStubSecurityProperties((Stub) jobPort);

    GetMultipleResourceProperties_Element request = new GetMultipleResourceProperties_Element();
    if(singleJob) {
      logger.debug("Including exitCode in the RP query.");

      request.setResourceProperty(new QName[] {ManagedJobConstants.RP_STATE, ManagedJobConstants.RP_HOLDING, ManagedJobConstants.RP_FAULT, ManagedExecutableJobConstants.RP_EXIT_CODE});
    } else {
      request.setResourceProperty(new QName[] {ManagedJobConstants.RP_STATE, ManagedJobConstants.RP_HOLDING, ManagedJobConstants.RP_FAULT});
    }
    GetMultipleResourcePropertiesResponse response = jobPort.getMultipleResourceProperties(request);

    SOAPElement[] any = response.get_any();
    if(logger.isInfoEnabled()) {
      logger.info("Raw status query response message:\n" + AnyHelper.toSingleString((MessageElement[]) any));
    }

    logger.debug("Deserializing \"state\".");
    StateEnumeration state = (StateEnumeration) ObjectDeserializer.toObject(any[0], StateEnumeration.class);

    logger.debug("Deserializing \"holding\".");
    Boolean holding = (Boolean) ObjectDeserializer.toObject(any[1], Boolean.class);

    int exitCodeIndex = 0;
    if(state.equals(StateEnumeration.Failed)) {
      logger.debug("Deserializing \"fault\".");

      // set the fault
      FaultType fault = deserializeFaultRP(any[2]);
      this.setFault(fault);

      // where to find the exit code
      exitCodeIndex = 3;
    } else {
      // where to find the exit code
      exitCodeIndex = 2;
    }

    if((state.equals(StateEnumeration.StageOut) || state.equals(StateEnumeration.Done) || state.equals(StateEnumeration.Failed)) && (exitCodeIndex > 0) && singleJob && (any.length == (exitCodeIndex + 1))) {
      logger.debug("Deserializing \"exitCode\".");

      Integer exitCodeWrapper = (Integer) ObjectDeserializer.toObject(any[exitCodeIndex], Integer.class);
      logger.debug("Fetched exitCode value is " + exitCodeWrapper);
      this.exitCode = exitCodeWrapper.intValue();
    }

    this.setState(state, holding.booleanValue());
  }

  /**
   * Gets submitted RSL from remote Managed Job Service. It is actually not only the final, but substituted RSL. To obtain it call <code>getRSLAttributes</code> afterwards. <b>Precondition</b>job has been submitted
   */
  private void refreshRSLAttributes() throws Exception {
    ManagedJobPortType jobPort = ManagedJobClientHelper.getPort(this.jobEndpointReference);

    setStubSecurityProperties((Stub) jobPort);

    GetResourcePropertyResponse response = jobPort.getResourceProperty(ManagedJobConstants.RP_SERVICE_LEVEL_AGREEMENT);

    SOAPElement[] any = response.get_any();
    ServiceLevelAgreementType sla = (ServiceLevelAgreementType) ObjectDeserializer.toObject(any[0], ServiceLevelAgreementType.class);
    this.jobDescription = sla.getJob();
    if(this.jobDescription == null) {
      this.jobDescription = sla.getMultiJob();
    }
  }

  public static List getJobs(final EndpointReferenceType factoryEndpoint) throws Exception {
    throw new RuntimeException("NOT IMPLEMENTED YET");
  }

  private void setStubSecurityProperties(final Stub stub) {
    if(logger.isDebugEnabled()) {
      logger.debug("setting factory stub security...using authz method " + getAuthorization());
    }

    ClientSecurityDescriptor secDesc = new ClientSecurityDescriptor();

    // set security type
    // Old version
    /*
     * if (this.securityType.equals(Constants.GSI_SEC_MSG)) { secDesc.setGSISecureMsg(this.getMessageProtectionType()); } else if (this.securityType.equals(Constants.GSI_SEC_CONV)) { secDesc.setGSISecureConv(this.getMessageProtectionType()); } else {
     * secDesc.setGSITransport(this.getMessageProtectionType()); }
     */

    // new version
    if(this.securityType.equals(Constants.GSI_SEC_MSG) || this.messageProtectionTypeOverride != null) {
      secDesc.setGSISecureMsg(this.messageProtectionTypeOverride == null ? getMessageProtectionType() : this.messageProtectionTypeOverride);
    }
    if(this.securityType.equals(Constants.GSI_SEC_CONV)) {
      secDesc.setGSISecureConv(this.getMessageProtectionType());
    }
    if(!(this.securityType.equals(Constants.GSI_SEC_MSG) || this.securityType.equals(Constants.GSI_SEC_CONV)) || this.transportProtectionTypOverride != null) {
      secDesc.setGSITransport(this.transportProtectionTypOverride == null ? getMessageProtectionType() : this.transportProtectionTypOverride);
    }

    // set authorization
    secDesc.setAuthz(getAuthorization());

    if(this.proxy != null) {
      // set proxy credential
      secDesc.setGSSCredential(this.proxy);
    }

    stub._setProperty(Constants.CLIENT_DESCRIPTOR, secDesc);
  }

  public void setAuthorization(final Authorization authz) {
    this.authorization = authz;
  }

  public Authorization getAuthorization() {
    return this.authorization;
  }

  public void setSecurityType(final String securityType) {
    this.securityType = securityType;
  }

  public String getSecurityType() {
    return this.securityType;
  }

  public void setMessageProtectionType(final Integer protectionType) {
    this.msgProtectionType = protectionType;
  }

  public Integer getMessageProtectionType() {
    return this.msgProtectionType;
  }

  public String getDelegationLevel() {
    return (this.limitedDelegation) ? GSIConstants.GSI_MODE_LIMITED_DELEG : GSIConstants.GSI_MODE_FULL_DELEG;
  }

  public void setDelegationEnabled(final boolean delegationEnabled) {
    this.delegationEnabled = delegationEnabled;
  }

  public boolean isDelegationEnabled() {
    return this.delegationEnabled;
  }

  // ============== REQUESTED TERMINATION TIME
  // ================================

  /**
   * The default lifetime of the resource is 24 hours.
   * 
   * @param duration
   *          the duration after which the job service should be destroyed. The hours and minutes will be used.
   */
  public void setDuration(final Date duration) {
    this.duration = duration;
  }

  /***
   * @param dateTime
   *          the date/time desired for termination of this job service
   */
  public void setTerminationTime(final Date termTime) {
    this.terminationDate = termTime;
  }

  /**
   * get termination time of managed job service based on parameters specified as JavaBean properties on this object. <b>Precondition</b>job has been requested
   * 
   * @throws Exception
   * @return Calendar
   */
  private Calendar getTerminationTime() throws Exception {
    Calendar terminationTime;

    if(this.duration == null && this.terminationDate == null) {
      terminationTime = getDefaultTerminationTime();
    } else {
      terminationTime = Calendar.getInstance();

      if(this.duration != null) {
        // add duration to termination time
        Calendar durationCalendar = Calendar.getInstance();
        durationCalendar.setTime(this.duration);
        int hours = durationCalendar.get(Calendar.HOUR_OF_DAY);
        int minutes = durationCalendar.get(Calendar.MINUTE);
        terminationTime.add(Calendar.HOUR_OF_DAY, hours);
        terminationTime.add(Calendar.MINUTE, minutes);
      } else {
        terminationTime.setTime(this.terminationDate);
      }

    }

    return terminationTime;
  }

  /**
   * Set TerminationTime RP of managed job service based on parameters specified as JavaBean properties on this object. <b>Precondition</b>job has been requested
   * 
   * @throws Exception
   * @return Calendar
   */
  public void setServiceTerminationTime() throws Exception {
    Calendar terminationTime = getTerminationTime();

    logger.debug("setting job resource duration");

    SetTerminationTime request = new SetTerminationTime();
    request.setRequestedTerminationTime(terminationTime);

    SetTerminationTimeResponse response = ManagedJobClientHelper.getPort(this.jobEndpointReference).setTerminationTime(request);

    if(logger.isDebugEnabled()) {
      Calendar newTermTime = response.getNewTerminationTime();
      logger.debug("requested: " + terminationTime.getTime());
      logger.debug("scheduled: " + newTermTime.getTime());
    }
  }

  private static void addDefaultDurationTo(final Calendar currentTime) {
    currentTime.add(Calendar.HOUR, DEFAULT_DURATION_HOURS);
  }

  private static Calendar getDefaultTerminationTime() {
    Calendar timeOut = Calendar.getInstance();
    addDefaultDurationTo(timeOut);
    return timeOut;
    /**
     * @todo if no term time, infinite or 24 hours?
     */
  }

  public boolean isSingleJob() {
    AttributedURI address = this.jobEndpointReference.getAddress();
    String path = address.getPath();
    if(path.indexOf("ManagedExecutableJobService") > 0) {
      return true;
    }

    return false;
  }

  public boolean isMultiJob() {
    AttributedURI address = this.jobEndpointReference.getAddress();
    String path = address.getPath();
    if(path.indexOf("ManagedMultiJobService") > 0) {
      return true;
    }

    return false;
  }

  public EndpointReferenceType getNotificationConsumerEPR() {
    return notificationConsumerEPR;
  }

  public void setNotificationConsumerEPR(final EndpointReferenceType notificationConsumerEPR) {
    this.useDefaultNotificationConsumer = false;
    try {
      if(notificationConsumerEPR != null) {
        this.notificationConsumerEPR = (EndpointReferenceType) ObjectSerializer.clone(notificationConsumerEPR);
      }
    } catch(Exception e) {
      this.notificationConsumerEPR = notificationConsumerEPR;
    }
  }
}
