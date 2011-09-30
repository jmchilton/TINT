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

package edu.umn.msi.tropix.common.jobqueue.utils;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;
import org.globus.exec.generated.ExtensionsType;
import org.globus.exec.generated.JobDescriptionType;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import org.xml.sax.InputSource;

import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class JobDescriptionUtils {
  public static final QName JOB_DESCRIPTION_QNAME = JobDescriptionType.getTypeDesc().getXmlType();

  public static String serialize(final JobDescriptionType jobDescription) {
    final StringWriter stringWriter = new StringWriter();
    try {
      ObjectSerializer.serialize(stringWriter, jobDescription, JOB_DESCRIPTION_QNAME);
    } catch(final SerializationException e) {
      throw new IllegalStateException("Failed to serialize jobDescription " + jobDescription, e);
    }
    return stringWriter.toString();
  }

  public static JobDescriptionType deserialize(final String jobDescriptionXml) {
    try {
      final InputSource xmlSource = new InputSource(new ByteArrayInputStream(jobDescriptionXml.getBytes()));
      return (JobDescriptionType) ObjectDeserializer.deserialize(xmlSource, JobDescriptionType.class);
    } catch(final DeserializationException e) {
      throw new IllegalStateException("Failed to deserialize jobDescription " + jobDescriptionXml, e);
    }
  }

  public static void addExtensionMessageElement(final JobDescriptionType jobDescription, final MessageElement messageElement) {
    addOrReplaceMessageElement(jobDescription, messageElement);
  }

  private static void addOrReplaceMessageElement(final JobDescriptionType jobDescription, final MessageElement newElement) {
    final String elementName = newElement.getName();
    ExtensionsType extensions = jobDescription.getExtensions();
    if(extensions == null) {
      extensions = new ExtensionsType();
      jobDescription.setExtensions(extensions);
    }
    MessageElement[] elements = extensions.get_any();
    boolean found = false;
    if(elements != null) {
      for(int i = 0; i < elements.length; i++) {
        if(elements[i].getName().equals(elementName)) {
          elements[i] = newElement;
          found = true;
        }
      }
    } else {
      elements = new MessageElement[0];
    }

    if(!found) {
      // Java 6
      // MessageElement[] newElements = Arrays.copyOf(elements, elements.length +1, MessageElement[].class);
      final MessageElement[] newElements = new MessageElement[elements.length + 1];
      for(int i = 0; i < elements.length; i++) {
        newElements[i] = elements[i];
      }
      newElements[elements.length] = newElement;
      extensions.set_any(newElements);
    }
  }

  public static void setExtensionParameter(final JobDescriptionType jobDescription, final String parameterName, final String value) {
    final MessageElement element = new MessageElement();
    element.setName(parameterName);
    element.setValue(value);
    addOrReplaceMessageElement(jobDescription, element);
  }

  public static MessageElement getExtensionElement(final JobDescriptionType jobDescription, final String elementName) {
    final ExtensionsType extensions = jobDescription.getExtensions();
    MessageElement extensionElement = null;
    if(extensions != null) {
      final MessageElement[] elements = extensions.get_any();
      if(elements != null) {
        for(final MessageElement element : elements) {
          final String name = element.getName();
          if(name.equals(elementName)) {
            extensionElement = element;
          }
        }
      }
    }
    return extensionElement;
  }

  public static String getExtensionParameter(final JobDescriptionType jobDescription, final String parameterName) {
    final MessageElement messageElement = getExtensionElement(jobDescription, parameterName);
    String value = null;
    if(messageElement != null) {
      value = messageElement.getValue();
    }
    return value;
  }

  public static void setStagingDirectory(final JobDescriptionType jobDescription, final String path) {
    setExtensionParameter(jobDescription, "stagingDirectory", path);
  }

  public static String getStagingDirectory(final JobDescriptionType jobDescription) {
    return getExtensionParameter(jobDescription, "stagingDirectory");
  }

  public static void setExecutionType(final JobDescriptionType jobDescription, final String executionType) {
    setExtensionParameter(jobDescription, "executionType", executionType);
  }

  public static String getExecutionType(final JobDescriptionType jobDescription) {
    return getExtensionParameter(jobDescription, "executionType");
  }

  public static void setJobType(final JobDescriptionType jobDescription, final String jobType) {
    setExtensionParameter(jobDescription, "jobType", jobType);
  }

  public static String getJobType(final JobDescriptionType jobDescription) {
    return getExtensionParameter(jobDescription, "jobType");
  }

  public static void setLocalJobId(final JobDescriptionType jobDescription, final String localJobId) {
    setExtensionParameter(jobDescription, "localJobId", localJobId);
  }

  public static String getLocalJobId(final JobDescriptionType jobDescription) {
    return getExtensionParameter(jobDescription, "localJobId");
  }

  public static void setProxy(final JobDescriptionType jobDescription, @Nullable final Credential proxy) {
    if(proxy != null) {
      setExtensionParameter(jobDescription, "proxy", proxy.toString());
    }
  }

  @Nullable
  public static Credential getProxy(final JobDescriptionType jobDescription) {
    final String proxyStr = getExtensionParameter(jobDescription, "proxy");
    Credential credential = null;
    if(proxyStr != null) {
      credential = Credentials.fromString(proxyStr);
    }
    return credential;
  }
}
