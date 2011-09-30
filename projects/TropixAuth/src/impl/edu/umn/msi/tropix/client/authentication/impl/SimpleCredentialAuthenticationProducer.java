package edu.umn.msi.tropix.client.authentication.impl;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.client.authentication.CredentialAuthentication;
import edu.umn.msi.tropix.client.authentication.CredentialAuthenticationProducer;
import edu.umn.msi.tropix.grid.credentials.Credential;

// A simpler credential authority producer than used by webgui just for the ssh server.
//@ManagedBean
public class SimpleCredentialAuthenticationProducer implements CredentialAuthenticationProducer {

  private static final class CredentialAuthenticationImpl extends UsernamePasswordAuthenticationToken implements CredentialAuthentication, Serializable {
    private Credential credential;
    
    private CredentialAuthenticationImpl(final Credential credential, 
        final Authentication inputAuthentication, 
        final Collection<GrantedAuthority> authorities) {
      super(inputAuthentication.getPrincipal(), inputAuthentication.getCredentials(), authorities);
      this.credential = credential;
    }

    
    public Credential getCredential() {
      return credential;
    }
    
  }
  
  public CredentialAuthentication get(final Credential credential, 
                                      final Authentication inputAuthentication) {
    final Collection<GrantedAuthority> authorities = Lists.<GrantedAuthority>newArrayList(new GrantedAuthorityImpl("ROLE_USER"));
    return new CredentialAuthenticationImpl(credential, inputAuthentication, authorities);
  }

}
