package edu.umn.msi.tropix.storage.cagrid.service;

import edu.umn.msi.cagrid.introduce.interfaces.client.service.ImplementsForService;
import edu.umn.msi.tropix.storage.cagrid.service.TropixStorageConfiguration;
import edu.umn.msi.tropix.storage.cagrid.service.TropixStorageApplicationContext;
import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
@SuppressWarnings("all") public class TropixStorageImpl extends TropixStorageImplBase {
  /**
   * DO NOT REMOVE THIS COMMENT!
   * @SpringIntroduceExtension
   */
  java.lang.Object __springBean__1;

  /**
   * DO NOT REMOVE THIS COMMENT!
   * @SpringIntroduceExtension
   */
  @ImplementsForService(interfaces = {"edu.umn.msi.tropix.storage.service.TropixStorageService"})
  private java.lang.Object get__springBean__1() {
    if(__springBean__1 == null) 
      __springBean__1 =  (java.lang.Object) TropixStorageApplicationContext.get().getBean("storageService");
    return __springBean__1; 
  }
  /**
   * DO NOT REMOVE THIS COMMENT!
   * @SpringIntroduceExtension
   */
  private void __initSpring__(TropixStorageConfiguration configuration) throws RemoteException 
  {
    TropixStorageApplicationContext.init(configuration);
  }

	public TropixStorageImpl() throws RemoteException {
		super();
		try { __initSpring__(getConfiguration()); } catch(Exception e) { throw new RemoteException("Failed to initialize beans.", e); }
	}
	
  /**
   * DO NOT REMOVE THIS COMMENT!
   * @InterfacesIntroduceExtension
   */
  public boolean delete(java.lang.String arg1) throws RemoteException {
    return ((edu.umn.msi.tropix.storage.service.TropixStorageService)get__springBean__1()).delete(arg1);
  }

  /**
   * DO NOT REMOVE THIS COMMENT!
   * @InterfacesIntroduceExtension
   */
  public boolean exists(java.lang.String arg1) throws RemoteException {
    return ((edu.umn.msi.tropix.storage.service.TropixStorageService)get__springBean__1()).exists(arg1);
  }

  /**
   * DO NOT REMOVE THIS COMMENT!
   * @InterfacesIntroduceExtension
   */
  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference prepareDownload(java.lang.String arg1) throws RemoteException {
    return ((edu.umn.msi.tropix.storage.service.TropixStorageService)get__springBean__1()).prepareDownload(arg1);
  }

  /**
   * DO NOT REMOVE THIS COMMENT!
   * @InterfacesIntroduceExtension
   */
  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference prepareUpload(java.lang.String arg1) throws RemoteException {
    return ((edu.umn.msi.tropix.storage.service.TropixStorageService)get__springBean__1()).prepareUpload(arg1);
  }

  /**
   * DO NOT REMOVE THIS COMMENT!
   * @InterfacesIntroduceExtension
   */
  public boolean canDownload(java.lang.String arg1) throws RemoteException {
    return ((edu.umn.msi.tropix.storage.service.TropixStorageService)get__springBean__1()).canDownload(arg1);
  }

  /**
   * DO NOT REMOVE THIS COMMENT!
   * @InterfacesIntroduceExtension
   */
  public boolean canUpload(java.lang.String arg1) throws RemoteException {
    return ((edu.umn.msi.tropix.storage.service.TropixStorageService)get__springBean__1()).canUpload(arg1);
  }

  /**
   * DO NOT REMOVE THIS COMMENT!
   * @InterfacesIntroduceExtension
   */
  public boolean canDelete(java.lang.String arg1) throws RemoteException {
    return ((edu.umn.msi.tropix.storage.service.TropixStorageService)get__springBean__1()).canDelete(arg1);
  }

}

