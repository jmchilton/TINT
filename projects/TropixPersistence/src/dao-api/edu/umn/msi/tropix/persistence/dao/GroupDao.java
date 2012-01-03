package edu.umn.msi.tropix.persistence.dao;

import java.util.Collection;

import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.User;

public interface GroupDao extends Dao<Group> {

  Collection<User> getUsers(final String groupId);
  
}
