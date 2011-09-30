/**
 * HttpTransferResource.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Apr 28, 2006 (12:42:00 EDT) WSDL2Java emitter.
 */

package edu.umn.msi.tropix.transfer.types;

public class CloudTransferResource  extends edu.umn.msi.tropix.transfer.types.TransferResource  implements java.io.Serializable {
    private java.lang.String s3url;  // attribute
    private java.lang.String identifier; // attribute

    public CloudTransferResource() {
    }

    public CloudTransferResource(
				 java.lang.String s3url,
				 java.lang.String identifier
) {
           this.s3url = s3url;
	   this.identifier = identifier;
    }


    /**
     * Gets the s3url value for this CloudTransferResource.
     * 
     * @return s3url
     */
    public java.lang.String getS3url() {
        return s3url;
    }


    /**
     * Sets the s3url value for this CloudTransferResource.
     * 
     * @param s3url
     */
    public void setS3url(java.lang.String s3url) {
        this.s3url = s3url;
    }

    public java.lang.String getIdentifier() {
	return identifier;
    }

    public void setIdentifier(java.lang.String identifier) {
	this.identifier = identifier;
    }



    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CloudTransferResource)) return false;
        CloudTransferResource other = (CloudTransferResource) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.s3url==null && other.getS3url()==null) || 
             (this.s3url!=null &&
              this.s3url.equals(other.getS3url()))) &&
            ((this.identifier==null && other.getIdentifier()==null) ||
             (this.identifier!=null &&
              this.identifier.equals(other.getIdentifier())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getS3url() != null) {
            _hashCode += getS3url().hashCode();
        }
	if (getIdentifier() != null) {
	    _hashCode += getIdentifier().hashCode();
	}
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CloudTransferResource.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://msi.umn.edu/tropix/transfer", "CloudTransferResource"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("s3url");
        attrField.setXmlName(new javax.xml.namespace.QName("", "s3url"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
	attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("identifier");
        attrField.setXmlName(new javax.xml.namespace.QName("", "identifier"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
