package edu.umn.msi.tropix.client.credential.impl;

import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.ldap.authentication.AbstractLdapAuthenticator;
import org.springframework.util.Log4jConfigurer;
import org.testng.annotations.Test;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Optional;

import edu.umn.msi.tropix.client.credential.ConfiguredSslSocketFactory;

/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.ppolicy.PasswordPolicyControl;
import org.springframework.security.ldap.ppolicy.PasswordPolicyControlExtractor;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * An authenticator which binds as a user.
 * 
 * @author Luke Taylor
 * 
 * @see AbstractLdapAuthenticator
 */
class BindAuthenticator extends AbstractLdapAuthenticator {
  // ~ Static fields/initializers =====================================================================================

  private static final Log logger = LogFactory.getLog(BindAuthenticator.class);

  // ~ Constructors ===================================================================================================

  /**
   * Create an initialized instance using the {@link BaseLdapPathContextSource} provided.
   * 
   * @param contextSource
   *          the BaseLdapPathContextSource instance against which bind operations will be
   *          performed.
   * 
   */
  public BindAuthenticator(BaseLdapPathContextSource contextSource) {
    super(contextSource);
  }

  // ~ Methods ========================================================================================================

  public DirContextOperations authenticate(Authentication authentication) {
    DirContextOperations user = null;
    Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication, "Can only process UsernamePasswordAuthenticationToken objects");

    String username = authentication.getName();
    String password = (String) authentication.getCredentials();

    if(!StringUtils.hasLength(password)) {
      logger.debug("Rejecting empty password for user " + username);
      throw new BadCredentialsException(messages.getMessage("LdapAuthenticationProvider.emptyPassword", "Empty Password"));
    }

    // If DN patterns are configured, try authenticating with them directly
    for(String dn : getUserDns(username)) {
      user = bindWithDn(dn, username, password);

      if(user != null) {
        break;
      }
    }

    // Otherwise use the configured search object to find the user and authenticate with the returned DN.
    if(user == null && getUserSearch() != null) {
      DirContextOperations userFromSearch = getUserSearch().searchForUser(username);
      user = bindWithDn(userFromSearch.getDn().toString(), username, password);
    }

    if(user == null) {
      throw new BadCredentialsException(messages.getMessage("BindAuthenticator.badCredentials", "Bad credentials"));
    }

    return user;
  }

  private DirContextOperations bindWithDn(String userDnStr, String username, String password) {
    System.out.println("Attempting to bind as " + userDnStr);
    BaseLdapPathContextSource ctxSource = (BaseLdapPathContextSource) getContextSource();
    DistinguishedName userDn = new DistinguishedName(userDnStr);
    DistinguishedName fullDn = new DistinguishedName(userDn);
    fullDn.prepend(ctxSource.getBaseLdapPath());

    logger.debug("Attempting to bind as " + fullDn);

    DirContext ctx = null;
    try {
      ctx = getContextSource().getContext(fullDn.toString(), password);
      System.out.println("Have ctx");
      // Check for password policy control
      PasswordPolicyControl ppolicy = PasswordPolicyControlExtractor.extractControl(ctx);

      Attributes attrs = ctx.getAttributes(userDn, getUserAttributes());

      DirContextAdapter result = new DirContextAdapter(attrs, userDn, ctxSource.getBaseLdapPath());

      if(ppolicy != null) {
        result.setAttributeValue(ppolicy.getID(), ppolicy);
      }

      System.out.println("Returning result");

      return result;
    } catch(NamingException e) {
      e.printStackTrace();
      // This will be thrown if an invalid user name is used and the method may
      // be called multiple times to try different names, so we trap the exception
      // unless a subclass wishes to implement more specialized behaviour.
      if((e instanceof org.springframework.ldap.AuthenticationException) || (e instanceof org.springframework.ldap.OperationNotSupportedException)) {
        handleBindException(userDnStr, username, e);
      } else {
        throw e;
      }
    } catch(javax.naming.NamingException e) {
      e.printStackTrace();
      throw LdapUtils.convertLdapException(e);
    } finally {
      LdapUtils.closeContext(ctx);
    }

    return null;
  }

  /**
   * Allows subclasses to inspect the exception thrown by an attempt to bind with a particular DN.
   * The default implementation just reports the failure to the debug logger.
   */
  protected void handleBindException(String userDn, String username, Throwable cause) {
    if(logger.isDebugEnabled()) {
      logger.debug("Failed to bind as " + userDn + ": " + cause);
    }
  }
}

public class LdapCredentialProviderImplTest {
  private static final Log LOG = LogFactory.getLog(LdapCredentialProviderImplTest.class);

  @Test(groups = "interact")
  public void testLdap() throws Exception {
    testSpringSecurity();
    //testDirect();
  }

  private void testDirect() throws javax.naming.NamingException {
    Hashtable<String, String> env = getEnvironmentTemplate();
    //env.put(Context.SECURITY_PROTOCOL, "ssl");
    env.put(Context.SECURITY_PRINCIPAL, getFullyDistinguishedName(getEnvironmentTemplate(), "chilton"));
    env.put(Context.SECURITY_CREDENTIALS, "!");
    
    System.out.println("Moo");
    InitialDirContext ctx = new InitialDirContext(env);
  }

  private Hashtable<String, String> getEnvironmentTemplate() {
    Hashtable<String, String> env = new Hashtable<String, String>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    ConfiguredSslSocketFactory.setCustomSslSocketFactoryIfNeeded(env, Optional.of("/home/john/installcert/jssecacerts"));
    env.put(Context.PROVIDER_URL, "ldaps://localhost:10636");
    env.put(Context.SECURITY_AUTHENTICATION, "simple");
    return env;
  }

  private static String getFullyDistinguishedName(final Hashtable environment, final String userName) throws javax.naming.NamingException {
    String[] attributeIDs = {"cn"}; // {"dn"} ;
    String searchFilter = "(cn=chilton)";

    DirContext dirContext = null;
    try {
      dirContext = new InitialDirContext(environment);
    } catch(NamingException e1) {
      throw new RuntimeException("Error occured in connecting to the directory server");
    }

    SearchControls searchControls = new SearchControls();
    searchControls.setReturningAttributes(attributeIDs);
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

    String fullyDistinguishedName = null;
    NamingEnumeration searchEnum = null;
    try {
      searchEnum = dirContext.search("ou=people, ou=internal, dc=DTC", searchFilter, searchControls);
    } catch(NamingException e) {
      throw new RuntimeException("User Name doesnot exists");
    }
    try {
      dirContext.close();
    } catch(NamingException e) {
      e.printStackTrace();
    }

    try {
      while(searchEnum.hasMore()) {
        SearchResult searchResult = (SearchResult) searchEnum.next();
        fullyDistinguishedName = searchResult.getName() + ",ou=people, ou=internal, dc=DTC";
        return fullyDistinguishedName;
      }
    } catch(NamingException e) {
      throw new RuntimeException("User Name doesnot exists");
    }
    return null;
  }

  private void testSpringSecurity() throws Exception {
    org.apache.log4j.LogManager.resetConfiguration();
    Log4jConfigurer.initLogging("resources/test/log4j.properties");
    Logger logger = org.apache.log4j.Logger.getLogger(BindAuthenticator.class);
    System.out.println(logger.isDebugEnabled());
    logger.debug("FOo");
    // BaseLdapPathContextSource arg = new BaseLdapPathContextSource();
    // System.out.println(InputContexts.toString(InputContexts.forInputStream(getClass().getClassLoader().getResourceAsStream("log4j.properties"))));
    LOG.debug("Moo");
    final LdapContextSource source = new LdapContextSource();
    source.setUrl("ldaps://localhost:10636/");
    source.setBase("ou=people, ou=internal, dc=DTC");
    
    final Map<Object, Object> properties = Maps.newHashMap();
    ConfiguredSslSocketFactory.setCustomSslSocketFactoryIfNeeded(properties, Optional.of("/home/john/installcert/jssecacerts"));
    source.setBaseEnvironmentProperties(properties);
    source.afterPropertiesSet();
    // source.setAnonymousReadOnly(true);
    BindAuthenticator authenticator = new BindAuthenticator(source);

    //authenticator.setUserDnPatterns(new String[] {"cn={0}"});
    LdapUserSearch userSearch = new org.springframework.security.ldap.search.FilterBasedLdapUserSearch("", "(cn={0})", source);
    authenticator.setUserSearch(userSearch);
    authenticator.afterPropertiesSet();
    authenticator.authenticate(new UsernamePasswordAuthenticationToken("chilton", "!"));
  }

}
