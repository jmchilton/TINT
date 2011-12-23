package edu.umn.msi.tropix.storage.cagrid.common;

import javax.xml.namespace.QName;


@SuppressWarnings("all") public interface TropixStorageConstants {
	public static final String SERVICE_NS = "http://msi.umn.edu/tropix/storage/cagrid/TropixStorage";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "TropixStorageKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "TropixStorageResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName SERVICEMETADATA = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
	
}
