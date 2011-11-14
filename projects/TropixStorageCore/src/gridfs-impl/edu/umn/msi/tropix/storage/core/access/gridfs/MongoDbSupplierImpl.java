package edu.umn.msi.tropix.storage.core.access.gridfs;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Value;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.mongodb.DB;
import com.mongodb.Mongo;

@Named("storageMongoDbSupplier")
public class MongoDbSupplierImpl implements Supplier<DB> {
  public static final String DEFAULT_DATABASE_NAME = "tintStorage";
  private String hostname = "localhost";
  private int port = 27017;
  private String databaseName = DEFAULT_DATABASE_NAME;
  private boolean authenticate = false;
  private String username = "";
  private String password = "";
  private Supplier<Mongo> mongoSupplier = new Supplier<Mongo>() {

    public Mongo get() {
      try {
        return new Mongo(hostname, port);
      } catch(final Exception e) {
        throw new RuntimeException(e);
      }
    }
    
  };

  @VisibleForTesting
  void setMongoSupplier(final Supplier<Mongo> mongoSupplier) {
    this.mongoSupplier = mongoSupplier;
  }
  
  private void authenticate(final DB db) {
    if(authenticate) {
      if(!db.authenticate(username, password.toCharArray())) {
        throw new RuntimeException("Failed to authenticate access to mongo db for storage backend");
      }
    }
  }
    
  public DB get() {
    final DB db = mongoSupplier.get().getDB(databaseName);
    authenticate(db);
    return db;
  }

  @Value("${storage.mongo.hostname}")
  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  @Value("${storage.mongo.port}")
  public void setPort(int port) {
    this.port = port;
  }

  @Value("${storage.mongo.database}")
  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  @Value("${storage.mongo.authenticate}")
  public void setAuthenticate(boolean authenticate) {
    this.authenticate = authenticate;
  }

  @Value("${storage.mongo.username}")
  public void setUsername(String username) {
    this.username = username;
  }

  @Value("${storage.mongo.password}")
  public void setPassword(String password) {
    this.password = password;
  }

}
