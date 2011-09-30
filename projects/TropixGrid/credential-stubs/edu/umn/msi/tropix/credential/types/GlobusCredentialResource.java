/**
 * GlobusCredentialResource.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Apr 28, 2006 (12:42:00 EDT) WSDL2Java emitter.
 */

package edu.umn.msi.tropix.credential.types;

public class GlobusCredentialResource  extends edu.umn.msi.tropix.credential.types.CredentialResource  implements java.io.Serializable {
    private byte[] encodedCredential;  // attribute

    public GlobusCredentialResource() {
    }

    public GlobusCredentialResource(
           byte[] encodedCredential) {
           this.encodedCredential = encodedCredential;
    }


    /**
     * Gets the encodedCredential value for this GlobusCredentialResource.
     * 
     * @return encodedCredential
     */
    public byte[] getEncodedCredential() {
        return encodedCredential;
    }


    /**
     * Sets the encodedCredential value for this GlobusCredentialResource.
     * 
     * @param encodedCredential
     */
    public void setEncodedCredential(byte[] encodedCredential) {
        this.encodedCredential = encodedCredential;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GlobusCredentialResource)) return false;
        GlobusCredentialResource other = (GlobusCredentialResource) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.encodedCredential==null && other.getEncodedCredential()==null) || 
             (this.encodedCredential!=null &&
              java.util.Arrays.equals(this.encodedCredential, other.getEncodedCredential())));
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
        if (getEncodedCredential() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEncodedCredential());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEncodedCredential(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GlobusCredentialResource.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://msi.umn.edu/tropix/credential", "GlobusCredentialResource"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("encodedCredential");
        attrField.setXmlName(new javax.xml.namespace.QName("", "encodedCredential"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
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
