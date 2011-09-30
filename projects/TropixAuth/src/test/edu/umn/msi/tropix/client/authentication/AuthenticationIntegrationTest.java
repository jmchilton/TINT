package edu.umn.msi.tropix.client.authentication;

import javax.inject.Inject;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.FreshConfigTest;

@ContextConfiguration(locations = "testContext.xml")
public class AuthenticationIntegrationTest extends FreshConfigTest {

  @Inject
  private AuthenticationProvider authenticationProvider;
  
  @Test(groups = "spring")
  public void authentication() {
    final UsernamePasswordAuthenticationToken inputToken = new UsernamePasswordAuthenticationToken("admin", "admin");
    final Authentication outputToken = authenticationProvider.authenticate(inputToken);
    assert outputToken.isAuthenticated();
  }
  
  
}
