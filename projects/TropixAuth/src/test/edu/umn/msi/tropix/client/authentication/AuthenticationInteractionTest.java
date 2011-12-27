package edu.umn.msi.tropix.client.authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.inject.Inject;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(locations = "testContext.xml")
public class AuthenticationInteractionTest extends AbstractTestNGSpringContextTests {

  @Inject
  private AuthenticationProvider authenticationProvider;

  @Test(groups = "unit")
  public void testLogin() throws IOException {
    final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    System.out.print("username:  ");
    final String username = in.readLine();
    System.out.print("password:  ");
    final String password = in.readLine();
    final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
    final Authentication authentication = authenticationProvider.authenticate(token);
    assert authentication.isAuthenticated();
  }
  
}
