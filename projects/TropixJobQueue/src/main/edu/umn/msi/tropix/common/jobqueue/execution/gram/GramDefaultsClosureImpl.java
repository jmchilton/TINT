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

package edu.umn.msi.tropix.common.jobqueue.execution.gram;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;
import org.globus.exec.generated.JobDescriptionType;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

@ManagedResource
public class GramDefaultsClosureImpl implements Closure<JobDescriptionType> {
  private static final String GROUP = "resourceAllocationGroup";
  private static final String CPUS_PER_HOST = "cpusPerHost";
  private static final String HOST_COUNT = "hostCount";

  private Integer cpusPerHost = null;
  private Integer hostCount = null;
  private Long maxTime = null;
  private Long memory = null;

  private static DocumentBuilder getDocumentBuilder() {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    try {
      builder = factory.newDocumentBuilder();
    } catch(final ParserConfigurationException e) {
      throw ExceptionUtils.convertException(e);
    }
    return builder;
  }

  private MessageElement getMessageElement() {
    final Document document = getDocumentBuilder().newDocument();
    final Element resourceAllocationGroup = document.createElement(GramDefaultsClosureImpl.GROUP);

    if(cpusPerHost != null) {
      final Element cpusPerHostElement = document.createElement(GramDefaultsClosureImpl.CPUS_PER_HOST);
      cpusPerHostElement.appendChild(document.createTextNode(Integer.toString(cpusPerHost)));
      resourceAllocationGroup.appendChild(cpusPerHostElement);
    }

    if(hostCount != null) {
      final Element hostCountElement = document.createElement(GramDefaultsClosureImpl.HOST_COUNT);
      hostCountElement.appendChild(document.createTextNode(Integer.toString(hostCount)));
      resourceAllocationGroup.appendChild(hostCountElement);
    }

    return new MessageElement(resourceAllocationGroup);

  }

  public void apply(final JobDescriptionType jobDescription) {
    if(maxTime != null) {
      jobDescription.setMaxTime(maxTime);
    }
    if(memory != null) {
      jobDescription.setMaxMemory(new NonNegativeInteger("" + memory));
    }
    final MessageElement element = JobDescriptionUtils.getExtensionElement(jobDescription, GramDefaultsClosureImpl.GROUP);
    if(element == null) {
      JobDescriptionUtils.addExtensionMessageElement(jobDescription, this.getMessageElement());
    }
  }

  @ManagedAttribute
  public Integer getCpusPerHost() {
    return cpusPerHost;
  }

  @ManagedAttribute
  public void setCpusPerHost(final Integer cpusPerHost) {
    this.cpusPerHost = cpusPerHost;
  }

  @ManagedAttribute
  public Integer getHostCount() {
    return hostCount;
  }

  @ManagedAttribute
  public void setHostCount(final Integer hostCount) {
    this.hostCount = hostCount;
  }

  @ManagedAttribute
  public Long getMemory() {
    return this.memory;
  }

  @ManagedAttribute
  public void setMemory(final Long memory) {
    this.memory = memory;
  }

  @ManagedAttribute
  public Long getMaxTime() {
    return maxTime;
  }

  @ManagedAttribute
  public void setMaxTime(final Long maxTime) {
    this.maxTime = maxTime;
  }

}
