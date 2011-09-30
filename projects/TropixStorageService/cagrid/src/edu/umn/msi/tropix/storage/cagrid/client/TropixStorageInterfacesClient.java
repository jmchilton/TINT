package edu.umn.msi.tropix.storage.cagrid.client;

import java.rmi.RemoteException;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.gsi.GlobusCredential;

@SuppressWarnings("all") public class TropixStorageInterfacesClient implements edu.umn.msi.tropix.storage.service.TropixStorageService  {
  private TropixStorageClient caGridClient;

  public TropixStorageInterfacesClient(String url) throws MalformedURIException, RemoteException {
    initialize(new TropixStorageClient(url));
  }
  
  public TropixStorageInterfacesClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
    initialize(new TropixStorageClient(url, proxy));
  }

  public TropixStorageInterfacesClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
    initialize(new TropixStorageClient(epr));
  }

  public TropixStorageInterfacesClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
    initialize(new TropixStorageClient(epr, proxy));
  }
  
  public TropixStorageInterfacesClient(TropixStorageClient caGridClient) {
    initialize(caGridClient);
  }
        
  private void initialize(TropixStorageClient caGridClient) {
    this.caGridClient = caGridClient;
  }		        

  public TropixStorageClient getCaGridClient() {
  	return caGridClient;
  }
       
  public boolean delete(java.lang.String arg1) throws java.rmi.RemoteException 
  {
    return caGridClient.delete(arg1);
  }
       
  public boolean exists(java.lang.String arg1) throws java.rmi.RemoteException 
  {
    return caGridClient.exists(arg1);
  }
       
  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference prepareDownload(java.lang.String arg1) throws java.rmi.RemoteException 
  {
    return caGridClient.prepareDownload(arg1);
  }
       
  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference prepareUpload(java.lang.String arg1) throws java.rmi.RemoteException 
  {
    return caGridClient.prepareUpload(arg1);
  }
       
  public boolean canDownload(java.lang.String arg1) throws java.rmi.RemoteException 
  {
    return caGridClient.canDownload(arg1);
  }
       
  public boolean canUpload(java.lang.String arg1) throws java.rmi.RemoteException 
  {
    return caGridClient.canUpload(arg1);
  }
       
  public boolean canDelete(java.lang.String arg1) throws java.rmi.RemoteException 
  {
    return caGridClient.canDelete(arg1);
  }
  
}
