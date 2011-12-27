package edu.umn.msi.tropix.client.credential.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.ldap.authentication.LdapAuthenticator;

import com.google.common.base.Optional;

import edu.umn.msi.tropix.client.authentication.config.Ldap;
import edu.umn.msi.tropix.client.credential.CredentialCreationOptions;
import edu.umn.msi.tropix.client.credential.CredentialProvider;
import edu.umn.msi.tropix.client.credential.InvalidUsernameOrPasswordException;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class LdapCredentialProviderImpl implements CredentialProvider {
  private LdapAuthenticatorFactory ldapAuthenticator;
  private Optional<String> identityPrefix;
  
  public LdapCredentialProviderImpl(final LdapAuthenticatorFactory ldapAuthenticatorFactory) {
    this.ldapAuthenticator = ldapAuthenticatorFactory;
  }
  
  
  public Credential getGlobusCredential(final String username, 
                                        final String password, 
                                        final CredentialCreationOptions options) throws InvalidUsernameOrPasswordException {
    final LdapAuthenticator authenticator = ldapAuthenticator.get(options.getAuthenicationSource(Ldap.class));
    final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
    try {
      authenticator.authenticate(token);
    } catch(final Exception e) {
      throw new InvalidUsernameOrPasswordException(e);
    }
    return Credentials.getMock(identityPrefix.or("") + username);
  }
  
  @Value("${auth.ldap.identity.prefix}")
  public void setIdentityPrefix(final String identityPrefix) {
    this.identityPrefix = Optional.fromNullable(identityPrefix);
  }

}
