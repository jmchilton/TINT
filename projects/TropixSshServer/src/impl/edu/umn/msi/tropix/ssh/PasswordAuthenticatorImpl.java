package edu.umn.msi.tropix.ssh;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.common.Session.AttributeKey;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.client.authentication.AuthenticationToken;
import edu.umn.msi.tropix.client.authentication.CredentialAuthentication;

@ManagedBean
public class PasswordAuthenticatorImpl implements PasswordAuthenticator {
  private static final Log LOG = LogFactory.getLog(PasswordAuthenticatorImpl.class);
  public static final AttributeKey CREDENTIAL_KEY = new AttributeKey();
  private final AuthenticationProvider authenticationProvider;
  private final FailedAttemptLogger failedAttemptLogger;
  private final String authenticationSource;

  @Inject
  public PasswordAuthenticatorImpl(final AuthenticationProvider authenticationProvider,
      final FailedAttemptLogger failedAttemptLogger,
      @Value("${ssh.authentication.source}") final String authenticationSource) {
    this.authenticationProvider = authenticationProvider;
    this.failedAttemptLogger = failedAttemptLogger;
    this.authenticationSource = authenticationSource;
  }

  public boolean authenticate(final String username,
      final String password,
      final ServerSession session) {
    LOG.debug(String.format("Attempting to authenticate username [%s] against auth source [%s]", username, authenticationSource));
    final AuthenticationToken usernamePasswordToken = new AuthenticationToken(username, password, authenticationSource);
    boolean isAuthenticated = false;
    try {
      final Authentication authentication = authenticationProvider.authenticate(usernamePasswordToken);
      Preconditions.checkState(authentication instanceof CredentialAuthentication);
      isAuthenticated = authentication.isAuthenticated();
      LOG.info("Authenticated? " + isAuthenticated + " " + authentication);
      if(isAuthenticated) {
        session.setAttribute(CREDENTIAL_KEY, ((CredentialAuthentication) authentication).getCredential());
      }
      return isAuthenticated;
    } finally {
      if(!isAuthenticated) {
        failedAttemptLogger.logFailedAttempt(username, password, session.getIoSession());
      }
    }
  }

}
