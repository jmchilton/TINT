package edu.umn.msi.tropix.common.spring;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.flyway.core.Flyway;

public class DatabaseMigrator extends Flyway {
  private static final Log LOG = LogFactory.getLog(DatabaseMigrator.class);
  private String url;
  private boolean migrate = true;
  
  public void setUrl(final String url) {
    this.url = url;
  }
  
  @PostConstruct
  public void migrateDatabase() {
    if(migrate) {
      LOG.info("Attempting to migrate database with JDBC URL : " + url);
      getPlaceholders().put("if_mysql", commentUnlessUrlContains("jdbc:mysql"));
      migrate();
    }
  }
  
  private String commentUnlessUrlContains(final String substring) {
    return url.contains(substring) ? "" : "--";
  }
  
  public void setMigrate(final boolean migrate) {
    this.migrate = migrate;
  }
    
}
