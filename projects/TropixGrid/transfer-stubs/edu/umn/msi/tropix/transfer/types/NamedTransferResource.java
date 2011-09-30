/**
 * HttpTransferResource.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Apr 28, 2006 (12:42:00 EDT) WSDL2Java emitter.
 */

package edu.umn.msi.tropix.transfer.types;

public class NamedTransferResource  extends edu.umn.msi.tropix.transfer.types.TransferResource  implements java.io.Serializable {
    private java.lang.String name;  // attribute
    private edu.umn.msi.tropix.transfer.types.TransferResource delegateTransferResource;

    public NamedTransferResource() {
    }

    public NamedTransferResource(
				 java.lang.String name,
				 edu.umn.msi.tropix.transfer.types.TransferResource delegateTransferResource) {
           this.name = name;
	   this.delegateTransferResource = delegateTransferResource;
    }


    /**
     * Gets the name value for this NamedTransferResource.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this NamedTransferResource.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    public edu.umn.msi.tropix.transfer.types.TransferResource getDelegateTransferResource() {
	return delegateTransferResource;
    }

    public void setDelegateTransferResource(edu.umn.msi.tropix.transfer.types.TransferResource delegateTransferResource) {
	this.delegateTransferResource = delegateTransferResource;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NamedTransferResource)) return false;
        NamedTransferResource other = (NamedTransferResource) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
	    ((this.delegateTransferResource == null && other.getDelegateTransferResource() == null) ||
	     (this.delegateTransferResource != null &&
	      this.delegateTransferResource.equals(other.getDelegateTransferResource())));
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
	if (getDelegateTransferResource() != null) {
	    _hashCode += getDelegateTransferResource().hashCode();
	}
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(NamedTransferResource.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://msi.umn.edu/tropix/transfer", "NamedTransferResource"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("name");
        attrField.setXmlName(new javax.xml.namespace.QName("", "name"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);

        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("delegateTransferResource");
        elemField.setXmlName(new javax.xml.namespace.QName("http://msi.umn.edu/tropix/transfer", "TransferResource"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://msi.umn.edu/tropix/transfer", "TransferResource"));
        typeDesc.addFieldDesc(elemField);
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
