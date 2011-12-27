package edu.umn.msi.tropix.client.credential.impl;

import org.springframework.security.ldap.authentication.LdapAuthenticator;

import edu.umn.msi.tropix.client.authentication.config.Ldap;

public interface LdapAuthenticatorFactory {

  LdapAuthenticator get(final Ldap ldapConfiguration);
  
}
