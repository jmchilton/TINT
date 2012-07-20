package edu.umn.msi.tropix.persistence.dao.hibernate;

import java.util.Collection;

import javax.annotation.ManagedBean;
import javax.inject.Named;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.dao.GroupDao;

@ManagedBean
@Named("groupDao")
public class GroupDaoImpl extends GenericDaoImpl<Group> implements GroupDao {

  public Collection<User> getUsers(final String groupId) {
    Preconditions.checkNotNull(groupId, "getUsers sent null groupId");
    return ImmutableList.copyOf(load(groupId).getUsers());
  }

}
