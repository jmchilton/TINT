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

package edu.umn.msi.tropix.common.jobqueue.extension;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputs;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputsInput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodsType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertiesListType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.beans.service.ResourceFrameworkOptions;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.beans.service.ServicesType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ProviderTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionPostProcessor;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

public class CreationExtensionPostProcessorImpl implements CreationExtensionPostProcessor {
  private static final String EXTENSION_NAME = "jobqueue";
  private static final List<NamespaceInfo> NAMESPACES = new ArrayList<NamespaceInfo>(6);
  private static final QName QUEUE_STATUS_METADATA_QNAME = new QName("http://msi.umn.edu/tropix/common/jobqueue/queueStatus", "QueueStatus");
  private static final QName TICKET_QNAME = new QName("http://msi.umn.edu/tropix/common/jobqueue/ticket", "Ticket");

  private static class NamespaceInfo {
    private final String fileName, packageName;

    public NamespaceInfo(final String fileName, final String packageName, final String namespace) {
      this.fileName = fileName;
      this.packageName = packageName;
    }

  }

  static {
    NAMESPACES.add(new NamespaceInfo("caGrid_Transfer.xsd", "org.cagrid.transfer.descriptor", null));
    NAMESPACES.add(new NamespaceInfo("DelegatedCredentialTypes.xsd", "org.cagrid.gaards.cds.delegated.stubs.types", null));
    NAMESPACES.add(new NamespaceInfo("TransferServiceContextTypes.xsd", "org.cagrid.transfer.context.stubs.types", null));
    NAMESPACES.add(new NamespaceInfo("jobQueueContextTicket.xsd", "edu.umn.msi.tropix.common.jobqueue.ticket", null));
    NAMESPACES.add(new NamespaceInfo("queueStatus.xsd", "edu.umn.msi.tropix.common.jobqueue.queuestatus", null));
    NAMESPACES.add(new NamespaceInfo("status.xsd", "edu.umn.msi.tropix.common.jobqueue.status", null));
    NAMESPACES.add(new NamespaceInfo("transfer.xsd", "edu.umn.msi.tropix.transfer.types", null));
    NAMESPACES.add(new NamespaceInfo("credential_resource.xsd", "edu.umn.msi.tropix.credential.types", null));
  }

  public void postCreate(final ServiceExtensionDescriptionType extensionDescription, final ServiceInformation serviceInformation) throws CreationExtensionException {
    final Properties serviceProperties = serviceInformation.getIntroduceServiceProperties();
    final ServiceDescription serviceDescription = serviceInformation.getServiceDescriptor();

    // grab schemas and copy them into the service's directory
    try {
      copyMetadataSchemas(getServiceSchemaDir(serviceProperties));

      System.out.println(System.getProperty("introduce.skeleton.service.name"));
      // copy libraries into the new lib directory
      copyLibraries(serviceProperties);

      // add the namespace types
      addNamespaces(serviceDescription, getServiceSchemaDir(serviceProperties));

      System.out.println("Adding queue status metadata");
      addQueueStatusResourceProperty(serviceDescription);

      System.out.println("Creating job context");
      createJobContext(serviceInformation);

      addFactoryMethods(serviceInformation);

      writeFactoryClasses(serviceInformation);

      handleTemplate(serviceInformation);

      fixWsdd(serviceInformation);

    } catch(final Exception e) {
      e.printStackTrace();
      throw new CreationExtensionException(e);
    }

  }
  
  private static final Map<String, String> POLYMORPHIC_CLASSES_TO_NAMESPACE_MAPPING = new HashMap<String, String>();
  private static final String TRANSFER_NAMESPACE = "http://msi.umn.edu/tropix/transfer";
  private static final String CREDENTIAL_NAMESPACE = "http://msi.umn.edu/tropix/credential";

  static {
    POLYMORPHIC_CLASSES_TO_NAMESPACE_MAPPING.put("edu.umn.msi.tropix.transfer.types.HttpTransferResource", TRANSFER_NAMESPACE);
    POLYMORPHIC_CLASSES_TO_NAMESPACE_MAPPING.put("edu.umn.msi.tropix.transfer.types.CaGridTransferResource", TRANSFER_NAMESPACE);
    POLYMORPHIC_CLASSES_TO_NAMESPACE_MAPPING.put("edu.umn.msi.tropix.transfer.types.GridFtpTransferResource", TRANSFER_NAMESPACE);

    POLYMORPHIC_CLASSES_TO_NAMESPACE_MAPPING.put("edu.umn.msi.tropix.credential.types.SimpleCredentialResource", CREDENTIAL_NAMESPACE);
    POLYMORPHIC_CLASSES_TO_NAMESPACE_MAPPING.put("edu.umn.msi.tropix.credential.types.GlobusCredentialResource", CREDENTIAL_NAMESPACE);
    POLYMORPHIC_CLASSES_TO_NAMESPACE_MAPPING.put("edu.umn.msi.tropix.credential.types.CaGridDelegatedCredentialResource", CREDENTIAL_NAMESPACE);    
  }
  
  private static String getTypeMappingHack() {
    final StringBuilder builder = new StringBuilder();
    for(Map.Entry<String, String> classNamespaceEntry : POLYMORPHIC_CLASSES_TO_NAMESPACE_MAPPING.entrySet()) {
      final String className = classNamespaceEntry.getKey();
      final String namespace = classNamespaceEntry.getValue();
      final String tagName = className.substring(className.lastIndexOf('.') + 1);
      builder.append("  <typeMapping xmlns:ns=\"" + namespace + "\" qname=\"ns:"  + tagName
                     + "\" type=\"java:" + className
                     + "\" serializer=\"org.apache.axis.encoding.ser.BeanSerializerFactory\" deserializer=\"org.apache.axis.encoding.ser.BeanDeserializerFactory\" encodingStyle=\"\"/>\n");
    }
    return builder.toString();
  }
  

  private void fixWsdd(final ServiceInformation serviceInformation) throws IOException {
    final File wsddFile = new File(serviceInformation.getBaseDirectory(), "server-config.wsdd");
    final StringBuffer wsddFileBuffer = Utils.fileToStringBuffer(wsddFile);
    final int lastTagXmlIndex = wsddFileBuffer.indexOf("</deployment>");
    final String newMappings = getTypeMappingHack(); //"  <typeMapping xmlns:ns=\"http://msi.umn.edu/tropix/transfer\" qname=\"ns:HttpTransferResource\" type=\"java:edu.umn.msi.tropix.transfer.types.HttpTransferResource\" serializer=\"org.apache.axis.encoding.ser.BeanSerializerFactory\" deserializer=\"org.apache.axis.encoding.ser.BeanDeserializerFactory\" encodingStyle=\"\"/>  <typeMapping xmlns:ns=\"http://msi.umn.edu/tropix/transfer\" qname=\"ns:CaGridTransferResource\" type=\"java:edu.umn.msi.tropix.transfer.types.CaGridTransferResource\" serializer=\"org.apache.axis.encoding.ser.BeanSerializerFactory\" deserializer=\"org.apache.axis.encoding.ser.BeanDeserializerFactory\" encodingStyle=\"\"/>  <typeMapping xmlns:ns=\"http://msi.umn.edu/tropix/transfer\" qname=\"ns:GridFtpTransferResource\" type=\"java:edu.umn.msi.tropix.transfer.types.GridFtpTransferResource\" serializer=\"org.apache.axis.encoding.ser.BeanSerializerFactory\" deserializer=\"org.apache.axis.encoding.ser.BeanDeserializerFactory\" encodingStyle=\"\"/>";
    wsddFileBuffer.insert(lastTagXmlIndex, newMappings);
    Utils.stringBufferToFile(wsddFileBuffer, wsddFile.getAbsolutePath());
  }

  private void handleTemplate(final ServiceInformation serviceInformation) throws Exception {
    final File templateFile = new File(serviceInformation.getBaseDirectory(), "introduce-template.xml");
    if(templateFile.exists()) {
      final ServiceDescription description = (ServiceDescription) Utils.deserializeDocument(templateFile.getPath(), ServiceDescription.class);
      final NamespacesType namespaces = description.getNamespaces();
      if(namespaces != null && namespaces.getNamespace() != null) {
        final List<NamespaceType> newNamespaces = new ArrayList<NamespaceType>();
        final String schemaDir = getServiceSchemaDir(serviceInformation.getIntroduceServiceProperties());
        for(final NamespaceType namespaceOutline : namespaces.getNamespace()) {
          final NamespaceType namespace = CommonTools.createNamespaceType(schemaDir + File.separator + namespaceOutline.getLocation(), new File(schemaDir));
          namespace.setGenerateStubs(namespaceOutline.getGenerateStubs());
          namespace.setPackageName(namespaceOutline.getPackageName());
          newNamespaces.add(namespace);
        }
        addNamespaces(serviceInformation.getServiceDescriptor(), newNamespaces);
      }
      final ServicesType services = description.getServices();
      if(services != null && services.getService() != null && services.getService().length > 0) {
        final ServiceType mainService = services.getService(0);
        final ResourcePropertiesListType rps = mainService.getResourcePropertiesList();
        if(rps != null && rps.getResourceProperty() != null) {
          for(final ResourcePropertyType rp : rps.getResourceProperty()) {
            addResourceProperty(serviceInformation.getServices().getService(0), rp);
          }
        }
      }
    }

  }

  private void fillInPlaceholders(final StringBuffer buffer, final ServiceInformation serviceInformation) {
    final ServiceType contextService = serviceInformation.getServices().getService(1);
    final String name = contextService.getName();
    final String path = name.substring(0, 1).toLowerCase() + name.substring(1);
    final String referenceType = contextService.getPackageName() + ".stubs.types." + name + "Reference";
    final String resourceType = contextService.getPackageName() + ".service.globus.resource." + name + "Resource";
    final String resourceHomeType = resourceType + "Home";

    replaceAll(buffer, "@SERVICE_NAME@", serviceInformation.getServices().getService(0).getName());
    replaceAll(buffer, "@PATH@", path);
    replaceAll(buffer, "@REFRENCE_TYPE@", referenceType);
    replaceAll(buffer, "@RESOURCE_HOME_TYPE@", resourceHomeType);
    replaceAll(buffer, "@RESOURCE_TYPE@", resourceType);
    replaceAll(buffer, "@PACKAGE@", serviceInformation.getServices().getService(0).getPackageName());

  }

  private void writeFactoryClasses(final ServiceInformation serviceInformation) throws IOException {
    final File contextFactoryTemplate = new File(getExtensionPath() + "JobContextFactory.java.template");
    final StringBuffer contextFactoryContents = Utils.fileToStringBuffer(contextFactoryTemplate);
    fillInPlaceholders(contextFactoryContents, serviceInformation);

    final File clientFactoryTemplate = new File(getExtensionPath() + "JobClientFactoryImpl.java.template");
    final StringBuffer clientFactoryContents = Utils.fileToStringBuffer(clientFactoryTemplate);
    fillInPlaceholders(clientFactoryContents, serviceInformation);

    final File directory = new File(serviceInformation.getBaseDirectory(), "src" + File.separator + CommonTools.getPackageDir(serviceInformation.getServices().getService(0)) + File.separator);
    final File serviceDirecotry = new File(directory, "service");
    final File factoryFile = new File(serviceDirecotry, "JobContextFactory.java");
    Utils.stringBufferToFile(contextFactoryContents, factoryFile.getAbsolutePath());

    final File clientDirectory = new File(directory, "client");
    final File clientFactoryFile = new File(clientDirectory, "JobClientFactoryImpl.java");
    Utils.stringBufferToFile(clientFactoryContents, clientFactoryFile.getAbsolutePath());
  }

  private static void replaceAll(final StringBuffer buffer, final String regex, final CharSequence replacement) {
    int location = 0;
    final Pattern pattern = Pattern.compile(regex);
    Matcher matcher;
    while(true) {
      matcher = pattern.matcher(buffer);
      if(!matcher.find(location)) {
        break;
      }
      location = matcher.start();

      buffer.delete(location, location + matcher.group().length());
      buffer.insert(location, replacement);
      location += replacement.length();
      
    }
  }

  private void addFactoryMethods(final ServiceInformation serviceInformation) {
    final ServiceType mainService = serviceInformation.getServices().getService(0);
    final ServiceType context = serviceInformation.getServices().getService()[1];

    final QName jobContextQName = new QName(context.getNamespace() + "/types", context.getName() + "Reference");

    mainService.getMethods();

    final MethodType getMethod = new MethodType();
    getMethod.setName("getJob");
    final MethodTypeInputsInput input = new MethodTypeInputsInput();
    input.setQName(TICKET_QNAME);
    input.setName("ticket");
    getMethod.setInputs(new MethodTypeInputs(new MethodTypeInputsInput[] {input}));
    final MethodTypeOutput output = new MethodTypeOutput();
    output.setIsClientHandle(true);
    output.setClientHandleClass(context.getPackageName() + ".client." + context.getName() + "Client");
    output.setResourceClientIntroduceServiceName(context.getName());
    output.setIsCreatingResourceForClientHandle(true);
    output.setQName(jobContextQName);
    getMethod.setOutput(output);

    final MethodType createMethod = new MethodType();
    createMethod.setName("createJob");
    final MethodTypeOutput createOutput = new MethodTypeOutput();
    createOutput.setIsClientHandle(true);
    createOutput.setClientHandleClass(context.getPackageName() + ".client." + context.getName() + "Client");
    createOutput.setResourceClientIntroduceServiceName(context.getName());
    createOutput.setQName(jobContextQName);
    createMethod.setOutput(createOutput);

    ArrayList<MethodType> methodList;
    if(mainService.getMethods() == null || mainService.getMethods().getMethod() == null) {
      methodList = new ArrayList<MethodType>(2);
    } else {
      methodList = new ArrayList<MethodType>(Arrays.asList(mainService.getMethods().getMethod()));

    }
    methodList.add(getMethod);
    methodList.add(createMethod);
    mainService.setMethods(new MethodsType(methodList.toArray(new MethodType[methodList.size()])));

  }

  private void createJobContext(final ServiceInformation info) {
    final ServiceType jobContext = new ServiceType();
    final ServiceType mainService = info.getServices().getService(0);

    jobContext.setName(mainService.getName() + "JobContext");
    jobContext.setPackageName(mainService.getPackageName() + ".jobcontext");
    jobContext.setNamespace(mainService.getNamespace() + "/JobContext");
    jobContext.setResourceFrameworkOptions(new ResourceFrameworkOptions());
    // jobContext.getResourceFrameworkOptions().setCustom(new Custom());
    CommonTools.addService(info.getServices(), jobContext);

    final ResourcePropertyType ticketResourceProperty = new ResourcePropertyType();
    ticketResourceProperty.setPopulateFromFile(false);
    ticketResourceProperty.setRegister(true);
    ticketResourceProperty.setQName(TICKET_QNAME);

    ProviderTools.addLifetimeResourceProvider(jobContext, info);
    addResourceProperty(info.getServices().getService()[1], ticketResourceProperty);
  }

  public static void addResourceProperty(final ServiceType service, final ResourcePropertyType resourceProperty) {
    ResourcePropertiesListType propsList = service.getResourcePropertiesList();
    if(propsList == null) {
      propsList = new ResourcePropertiesListType();
      service.setResourcePropertiesList(propsList);
    }
    ResourcePropertyType[] resourcePropertyArray = propsList.getResourceProperty();
    if(resourcePropertyArray == null || resourcePropertyArray.length == 0) {
      resourcePropertyArray = new ResourcePropertyType[] {resourceProperty};
    } else {
      final ResourcePropertyType[] tmpArray = new ResourcePropertyType[resourcePropertyArray.length + 1];
      System.arraycopy(resourcePropertyArray, 0, tmpArray, 0, resourcePropertyArray.length);
      tmpArray[resourcePropertyArray.length] = resourceProperty;
      resourcePropertyArray = tmpArray;
    }
    propsList.setResourceProperty(resourcePropertyArray);
  }

  private void addQueueStatusResourceProperty(final ServiceDescription desc) {
    final ResourcePropertyType queueStatusResourceProperty = new ResourcePropertyType();
    queueStatusResourceProperty.setPopulateFromFile(false);
    queueStatusResourceProperty.setRegister(true);
    queueStatusResourceProperty.setQName(QUEUE_STATUS_METADATA_QNAME);
    addResourceProperty(desc.getServices().getService()[0], queueStatusResourceProperty);
  }

  private void copyLibraries(final Properties props) throws Exception {
    final String toDir = getServiceLibDir(props);
    final File directory = new File(toDir);
    if(!directory.exists()) {
      directory.mkdirs();
    }
    // from extension's lib directory
    final File libDir = new File(getExtensionPath() + File.separator + "lib");
    final File[] libs = libDir.listFiles(new FileFilter() {
      public boolean accept(final File pathname) {
        final String name = pathname.getName();
        return name.endsWith(".jar");
      }
    });

    if(libs != null) {
      final File[] copiedLibs = new File[libs.length];
      for(int i = 0; i < libs.length; i++) {
        final File outFile = new File(toDir + File.separator + libs[i].getName());
        copiedLibs[i] = outFile;
        Utils.copyFile(libs[i], outFile);
      }
      modifyClasspathFile(copiedLibs, props);
    }

  }

  private String getServiceLibDir(final Properties props) {
    return props.getProperty(IntroduceConstants.INTRODUCE_SKELETON_DESTINATION_DIR) + File.separator + "lib";
  }

  private String getServiceSchemaDir(final Properties props) {
    return props.getProperty(IntroduceConstants.INTRODUCE_SKELETON_DESTINATION_DIR) + File.separator + "schema" + File.separator + props.getProperty(IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME);
  }

  private void modifyClasspathFile(final File[] libs, final Properties props) throws Exception {
    final File classpathFile = new File(props.getProperty(IntroduceConstants.INTRODUCE_SKELETON_DESTINATION_DIR) + File.separator + ".classpath");
    ExtensionUtilities.syncEclipseClasspath(classpathFile, libs);
  }

  private void copyMetadataSchemas(final String schemaDir) throws Exception {
    final File extensionSchemaDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + EXTENSION_NAME + File.separator + "schema");
    final List<File> schemaFiles = Utils.recursiveListFiles(extensionSchemaDir, new FileFilters.XSDFileFilter());
    for(final File schemaFile : schemaFiles) {
      final String subname = schemaFile.getCanonicalPath().substring(extensionSchemaDir.getCanonicalPath().length() + File.separator.length());
      copySchema(subname, schemaDir);
    }
  }

  private void addNamespaces(final ServiceDescription description, final List<NamespaceType> newNamespaces) {
    NamespacesType namespaces = description.getNamespaces();
    if(namespaces == null) {
      namespaces = new NamespacesType();
    }
    final List<NamespaceType> allNamespaces = new ArrayList<NamespaceType>(Arrays.asList(namespaces.getNamespace()));
    allNamespaces.addAll(newNamespaces);

    // add those new namespaces to the list of namespace types
    final NamespaceType[] nsArray = new NamespaceType[allNamespaces.size()];
    allNamespaces.toArray(nsArray);
    namespaces.setNamespace(nsArray);
    description.setNamespaces(namespaces);
  }

  private void addNamespaces(final ServiceDescription description, final String schemaDir) throws Exception {
    System.out.println("Modifying namespace definitions");

    // add some namespaces to the service
    final List<NamespaceType> newNamespaces = new ArrayList<NamespaceType>();

    for(final NamespaceInfo namespaceInfo : NAMESPACES) {

      final NamespaceType namespace = CommonTools.createNamespaceType(schemaDir + File.separator + namespaceInfo.fileName, new File(schemaDir));
      namespace.setPackageName(namespaceInfo.packageName);
      namespace.setGenerateStubs(false);
      newNamespaces.add(namespace);
    }
    addNamespaces(description, newNamespaces);

  }

  private String getExtensionPath() {
    return ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + EXTENSION_NAME + File.separator;
  }

  private void copySchema(final String schemaName, final String outputDir) throws IOException {
    final File schemaFile = new File(getExtensionPath() + "schema" + File.separator + schemaName);
    System.out.println("Copying schema from " + schemaFile.getAbsolutePath());
    final File outputFile = new File(outputDir + File.separator + schemaName);
    System.out.println("Saving schema to " + outputFile.getAbsolutePath());
    Utils.copyFile(schemaFile, outputFile);
  }

}
