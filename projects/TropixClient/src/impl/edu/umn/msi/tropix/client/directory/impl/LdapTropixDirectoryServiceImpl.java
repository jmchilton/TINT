package edu.umn.msi.tropix.client.directory.impl;

import info.minnesotapartnership.tropix.directory.TropixDirectoryService;
import info.minnesotapartnership.tropix.directory.models.Person;

import java.util.Hashtable;
import java.util.LinkedList;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LdapTropixDirectoryServiceImpl implements TropixDirectoryService {
  protected String ldapUrl;
  protected String ldapBase;
  protected String filter = null;
  protected String userIdLabel;
  protected String userFirstNameLabel;
  protected String userLastNameLabel;
  protected String userEmailLabel;

  protected String gridIdPrefix;

  protected String getFilter() {
    return filter != null ? filter : (userIdLabel + "=*");
  }

  public Person[] getUsers() {
    Hashtable<String, String> env = new Hashtable<String, String>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, ldapUrl);
    env.put(Context.SECURITY_AUTHENTICATION, "none");
    LinkedList<Person> users = new LinkedList<Person>();
    try {
      DirContext ctx = new InitialDirContext(env);
      SearchControls ctls = new SearchControls();
      ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      String filter = getFilter();
      NamingEnumeration<SearchResult> answer = ctx.search(ldapBase, filter, ctls);
      while(answer.hasMore()) {

        SearchResult result = answer.next();
        Attributes attributes = result.getAttributes();
        if(attributes == null) {
          continue;
        }

        String userId = attributes.get(userIdLabel).get().toString();
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
