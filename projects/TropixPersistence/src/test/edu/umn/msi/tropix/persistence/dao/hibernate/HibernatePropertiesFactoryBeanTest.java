package edu.umn.msi.tropix.persistence.dao.hibernate;

import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.Test;

public class HibernatePropertiesFactoryBeanTest {

  @Test(groups = "unit")
  public void testRewritesHbm2DdlMigrate() throws IOException {
    final Properties properties = new Properties();
    properties.put("hibernate.hbm2ddl.auto", "migrate");
    final HibernatePropertiesFactoryBean bean = new HibernatePropertiesFactoryBean();
    bean.setProperties(properties);
    bean.afterPropertiesSet();
    assert bean.getObject().get("hibernate.hbm2ddl.auto").equals("verify");
  }
  
}
