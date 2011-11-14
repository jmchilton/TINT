package edu.umn.msi.tropix.storage.core.access.gridfs;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mongodb.DB;
import com.mongodb.Mongo;

public class MongoDbSupplierImplTest {

  @Test(groups = "unit")
  public void testDefaultPort() {
    assert getDefaultDb().getMongo().getAddress().getPort() == 27017;
  }
  
  @Test(groups = "unit")
  public void testDefaultHostname() {
    assert getDefaultDb().getMongo().getAddress().sameHost("localhost");
  }
  
  @Test(groups = "unit")
  public void testDefaultDatabase() {
    assert getDefaultDb().getName().equals(MongoDbSupplierImpl.DEFAULT_DATABASE_NAME);
  }
  
  @Test(groups = "unit")
  public void testSetDatabaseName() {
    assert getConfiguredDb().getName().equals("TEST_NAME");
  }
  
  @Test(groups = "unit")
  public void testSetPort() {
    assert getConfiguredDb().getMongo().getAddress().getPort() == 12345;
  }
  
  @Test(groups = "unit")
  public void testSetHostname() {
    assert getConfiguredDb().getMongo().getAddress().getHost().equals("foo.com");
  }

  private Supplier<Mongo> mockMongoSupplierFor(final DB db) {
    final Mongo mongo = EasyMock.createMock(Mongo.class);
    EasyMock.expect(mongo.getDB(EasyMock.<String>anyObject())).andStubReturn(db);
    EasyMock.replay(mongo);
    return Suppliers.ofInstance(mongo);
  }
  
  @Test(groups = "unit")
  public void testAuthenticate() {
    final DB mockDb = EasyMock.createMock(DB.class);
    final MongoDbSupplierImpl supplier = getAuthenticatedSupplier(mockDb, true);
    supplier.get();
    EasyMock.verify(mockDb);
  }

  private MongoDbSupplierImpl getAuthenticatedSupplier(final DB mockDb, final boolean authenticate) {
    EasyMock.expect(mockDb.authenticate(EasyMock.eq("moo"), EasyMock.aryEq("cow".toCharArray()))).andReturn(authenticate);
    EasyMock.replay(mockDb);
    final MongoDbSupplierImpl supplier = new MongoDbSupplierImpl();
    supplier.setMongoSupplier(mockMongoSupplierFor(mockDb));
    supplier.setAuthenticate(true);
    supplier.setUsername("moo");
    supplier.setPassword("cow");
    return supplier;
  }
  
  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testFailedAuthentication() {
    final DB mockDb = EasyMock.createMock(DB.class);
    final MongoDbSupplierImpl supplier = getAuthenticatedSupplier(mockDb, false);
    supplier.get();
  }

  private DB getConfiguredDb() {
    final MongoDbSupplierImpl supplier = new MongoDbSupplierImpl();
    supplier.setDatabaseName("TEST_NAME");
    supplier.setPort(12345);
    supplier.setHostname("foo.com");
    return supplier.get();
  }

  private DB getDefaultDb() {
    final MongoDbSupplierImpl supplier = new MongoDbSupplierImpl();
    final DB db = supplier.get();
    return db;
  }
  
}
