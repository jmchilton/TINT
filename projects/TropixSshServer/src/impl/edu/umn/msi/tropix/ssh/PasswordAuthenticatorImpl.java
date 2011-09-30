package edu.umn.msi.tropix.ssh;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

import edu.umn.msi.tropix.client.authentication.AuthenticationToken;

@ManagedBean
public class PasswordAuthenticatorImpl implements PasswordAuthenticator {
  private static final Log LOG = LogFactory.getLog(PasswordAuthenticatorImpl.class);
  private AuthenticationProvider authenticationProvider;

  @Inject
  public PasswordAuthenticatorImpl(final AuthenticationProvider authenticationProvider) {
    this.authenticationProvider = authenticationProvider;
  }

  public boolean authenticate(final String username, 
      final String password, 
      final ServerSession session) {
    LOG.debug(String.format("Attempting to authenticate username [%s]", username));
    final AuthenticationToken usernamePasswordToken = new AuthenticationToken(username, password, "Local");
    final Authentication authentication = authenticationProvider.authenticate(usernamePasswordToken);
    boolean isAuthenticated = authentication.isAuthenticated();
    LOG.info("Authenticated? " + isAuthenticated + " " + authentication);
    return isAuthenticated;
  }

}
