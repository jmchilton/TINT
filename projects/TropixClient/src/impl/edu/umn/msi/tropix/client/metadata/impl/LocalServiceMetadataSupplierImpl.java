package edu.umn.msi.tropix.client.metadata.impl;

import javax.xml.namespace.QName;

import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.ServiceMetadataServiceDescription;
import gov.nih.nci.cagrid.metadata.service.Service;

public class LocalServiceMetadataSupplierImpl extends LocalMetadataSupplierImpl<ServiceMetadata> {
  private static final QName SERVICE_METADATA_QNAME = QName.valueOf("{gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata}ServiceMetadata");

  public LocalServiceMetadataSupplierImpl(final String name) {
    super(ServiceMetadata.class);
    setQname(SERVICE_METADATA_QNAME);
    final ServiceMetadata serviceMetadata = new ServiceMetadata();
    final ServiceMetadataServiceDescription serviceDescription = new ServiceMetadataServiceDescription();
    Service service = new Service();
    service.setName(name);
    serviceDescription.setService(service);
    serviceMetadata.setServiceDescription(serviceDescription);
    super.setObject(serviceMetadata);
  }

}
