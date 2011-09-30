/**
 * CaGridDelegatedCredentialResource.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Apr 28, 2006 (12:42:00 EDT) WSDL2Java emitter.
 */

package edu.umn.msi.tropix.credential.types;

public class CaGridDelegatedCredentialResource  extends edu.umn.msi.tropix.credential.types.CredentialResource  implements java.io.Serializable {
    private org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference delegatedCredentialReference;

    public CaGridDelegatedCredentialResource() {
    }

    public CaGridDelegatedCredentialResource(
           org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference delegatedCredentialReference) {
           this.delegatedCredentialReference = delegatedCredentialReference;
    }


    /**
     * Gets the delegatedCredentialReference value for this CaGridDelegatedCredentialResource.
     * 
     * @return delegatedCredentialReference
     */
    public org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference getDelegatedCredentialReference() {
        return delegatedCredentialReference;
    }


    /**
     * Sets the delegatedCredentialReference value for this CaGridDelegatedCredentialResource.
     * 
     * @param delegatedCredentialReference
     */
    public void setDelegatedCredentialReference(org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference delegatedCredentialReference) {
        this.delegatedCredentialReference = delegatedCredentialReference;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CaGridDelegatedCredentialResource)) return false;
        CaGridDelegatedCredentialResource other = (CaGridDelegatedCredentialResource) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.delegatedCredentialReference==null && other.getDelegatedCredentialReference()==null) || 
             (this.delegatedCredentialReference!=null &&
              this.delegatedCredentialReference.equals(other.getDelegatedCredentialReference())));
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
        if (getDelegatedCredentialReference() != null) {
            _hashCode += getDelegatedCredentialReference().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CaGridDelegatedCredentialResource.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://msi.umn.edu/tropix/credential", "CaGridDelegatedCredentialResource"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("delegatedCredentialReference");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cds.gaards.cagrid.org/CredentialDelegationService/DelegatedCredential/types", "DelegatedCredentialReference"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cds.gaards.cagrid.org/CredentialDelegationService/DelegatedCredential/types", ">DelegatedCredentialReference"));
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
