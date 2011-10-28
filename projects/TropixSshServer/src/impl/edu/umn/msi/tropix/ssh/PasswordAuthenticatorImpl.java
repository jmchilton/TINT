package edu.umn.msi.tropix.ssh;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.common.Session.AttributeKey;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.client.authentication.AuthenticationToken;
import edu.umn.msi.tropix.client.authentication.CredentialAuthentication;

@ManagedBean
public class PasswordAuthenticatorImpl implements PasswordAuthenticator {
  private static final Log LOG = LogFactory.getLog(PasswordAuthenticatorImpl.class);
  public static final AttributeKey CREDENTIAL_KEY = new AttributeKey();
  private AuthenticationProvider authenticationProvider;

  @Inject
  public PasswordAuthenticatorImpl(final AuthenticationProvider authenticationProvider) {
    this.authenticationProvider = authenticationProvider;
  }

  public boolean authenticate(final String username, 
      final String password, 
      final ServerSession session) {
    session.getIoSession().getRemoteAddress();
    LOG.debug(String.format("Attempting to authenticate username [%s]", username));
    final AuthenticationToken usernamePasswordToken = new AuthenticationToken(username, password, "Local");
    final Authentication authentication = authenticationProvider.authenticate(usernamePasswordToken);
    Preconditions.checkState(authentication instanceof CredentialAuthentication);
    boolean isAuthenticated = authentication.isAuthenticated();
    LOG.info("Authenticated? " + isAuthenticated + " " + authentication);
    if(isAuthenticated) {
      session.setAttribute(CREDENTIAL_KEY, ((CredentialAuthentication) authentication).getCredential());
    }
    return isAuthenticated;
  }

}
