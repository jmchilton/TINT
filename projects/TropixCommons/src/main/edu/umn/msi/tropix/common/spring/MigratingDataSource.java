package edu.umn.msi.tropix.common.spring;

import javax.annotation.PostConstruct;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class MigratingDataSource extends DriverManagerDataSource {
  private final DatabaseMigrator migrator = new DatabaseMigrator();
  
  @Override
  public void setUrl(final String url) {
    super.setUrl(url);
    migrator.setUrl(url);
  }
  
  public void setMigrationPackage(final String packageName) {
    migrator.setBasePackage(packageName);
    migrator.setBaseDir(packageName.replaceAll("\\.", "/"));
  }
  
  public void setAutoDdl(final String autoDdl) {
    migrator.setMigrate(autoDdl.equals("migrate"));
  }
  
  @PostConstruct
  public void migrate() {
    migrator.setDataSource(this);
    migrator.migrateDatabase();
  }
  
}
