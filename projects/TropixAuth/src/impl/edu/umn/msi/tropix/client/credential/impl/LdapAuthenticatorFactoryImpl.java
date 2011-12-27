package edu.umn.msi.tropix.client.credential.impl;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.search.LdapUserSearch;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Optional;

import edu.umn.msi.tropix.client.authentication.config.Ldap;
import edu.umn.msi.tropix.client.credential.ConfiguredSslSocketFactory;

public class LdapAuthenticatorFactoryImpl implements LdapAuthenticatorFactory {
  
  public LdapAuthenticator get(final Ldap ldap) {
    final LdapContextSource source = new LdapContextSource();
    source.setUrl(ldap.getHost());
    source.setBase(ldap.getSearchBase());
    
    final Map<Object, Object> properties = Maps.newHashMap();
    ConfiguredSslSocketFactory.setCustomSslSocketFactoryIfNeeded(properties, Optional.of(ldap.getTruststore()));
    source.setBaseEnvironmentProperties(properties);
    initialize(source);

    final BindAuthenticator authenticator = new BindAuthenticator(source);
    final LdapUserSearch userSearch = new org.springframework.security.ldap.search.FilterBasedLdapUserSearch("", String.format("(%s={0})", ldap.getUserIdLabel()), source);
    authenticator.setUserSearch(userSearch);
    initialize(authenticator);

    return authenticator;
  }
  
  private void initialize(final InitializingBean bean) {
    try {
      bean.afterPropertiesSet();
    } catch(final RuntimeException e) {
      throw e;
    } catch(final Exception e) {
      throw new RuntimeException(e);
    }
  }

}
