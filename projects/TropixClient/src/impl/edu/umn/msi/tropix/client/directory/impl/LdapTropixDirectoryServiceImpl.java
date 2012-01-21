package edu.umn.msi.tropix.client.directory.impl;

import info.minnesotapartnership.tropix.directory.TropixDirectoryService;
import info.minnesotapartnership.tropix.directory.models.Person;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.util.StringUtils;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.client.credential.ConfiguredSslSocketFactory;

public class LdapTropixDirectoryServiceImpl implements TropixDirectoryService {
  private Optional<String> truststorePath;
  private String ldapUrl;
  private String ldapBase;
  private String filter = null;
  private String userIdLabel;
  private String userFirstNameLabel;
  private String userLastNameLabel;
  private String userEmailLabel;
  private String gridIdPrefix;
  private Iterable<String> ignoreIds;

  public void setIgnoreIds(final Iterable<String> ignoreIds) {
    this.ignoreIds = ignoreIds;
  }

  protected String getFilter() {
    return filter != null ? filter : (userIdLabel + "=*");
  }

  public Person[] getUsers() {
    Hashtable<String, String> env = new Hashtable<String, String>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    ConfiguredSslSocketFactory.setCustomSslSocketFactoryIfNeeded(env, truststorePath);
    env.put(Context.PROVIDER_URL, ldapUrl);
    env.put(Context.SECURITY_AUTHENTICATION, "none");
    LinkedList<Person> users = new LinkedList<Person>();
    try {
      DirContext ctx = new InitialDirContext(env);

      // Attributes attrs = ctx.getAttributes("cn=chilton, ou=people, ou=internal, dc=DTC");
      // NamingEnumeration<? extends Attribute> enumeration = attrs.getAll();
      // while(enumeration.hasMore()) {
      // Attribute attribute = enumeration.next();
      // System.out.println("Attribute: " + attribute.getID());
      // }

      SearchControls ctls = new SearchControls();
      ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      String filter = getFilter();
      NamingEnumeration<SearchResult> answer = ctx.search(ldapBase, filter, ctls);
      // boolean firstAttribute = true;
      final Set<String> ignoreIds = Sets.newHashSet(this.ignoreIds);
      while(answer.hasMore()) {

        SearchResult result = answer.next();
        Attributes attributes = result.getAttributes();
        if(attributes == null) {
          continue;
        }

        final String userId = attributes.get(userIdLabel).get().toString();

        // if(userId.contains("posokho0")) {
        // NamingEnumeration<? extends Attribute> enumeration = attributes.getAll();
        // while(enumeration.hasMore()) {
        // Attribute attribute = enumeration.next();
        // System.out.println("Attribute " + attribute.getID() + " value " + attribute.get().toString());
        // }
        // }

        if(ignoreIds.contains(userId)) {
          continue;
        }
        String userFirstName = attributes.get(userFirstNameLabel).get().toString();
        String userLastName = attributes.get(userLastNameLabel).get().toString();
        String userEmail = attributes.get(userEmailLabel).get().toString();
        // System.out.println(attributes.get("pwdChangedTime").get().toString());

        Person person = new Person();
        person.setCagridIdentity(gridIdPrefix + userId);
        person.setFirstName(userFirstName);
        person.setLastName(userLastName);
        person.setEmailAddress(userEmail);

        users.add(person);

      }
      return users.toArray(new Person[users.size()]);
    } catch(Throwable t) {
      t.printStackTrace();
      throw new IllegalStateException("Failed to fetch list of users", t);
    }
  }

  /**
   * Custom truststore for interacting with directory service.
   * 
   * @param truststorePat
   */
  public void setTruststorePath(final String truststorePath) {
    if(StringUtils.hasText(truststorePath)) {
      this.truststorePath = Optional.of(truststorePath);
    } else {
      this.truststorePath = Optional.absent();
    }
  }

  public void setLdapUrl(final String ldapUrl) {
    this.ldapUrl = ldapUrl;
  }

  public void setLdapBase(final String ldapBase) {
    this.ldapBase = ldapBase;
  }

  public void setUserIdLabel(final String userIdLabel) {
    this.userIdLabel = userIdLabel;
  }

  public void setUserFirstNameLabel(final String userFirstNameLabel) {
    this.userFirstNameLabel = userFirstNameLabel;
  }

  public void setUserLastNameLabel(final String userLastNameLabel) {
    this.userLastNameLabel = userLastNameLabel;
  }

  public void setUserEmailLabel(final String userEmailLabel) {
    this.userEmailLabel = userEmailLabel;
  }

  public void setGridIdPrefix(final String gridIdPrefix) {
    this.gridIdPrefix = gridIdPrefix;
  }

  public void setFilter(final String filter) {
    if(filter != null && filter.length() > 0) {
      this.filter = filter;
    }
  }

}
