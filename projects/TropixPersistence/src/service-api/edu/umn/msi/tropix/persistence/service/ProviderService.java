package edu.umn.msi.tropix.persistence.service;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.persistence.aop.Admin;
import edu.umn.msi.tropix.persistence.aop.AutoUser;
import edu.umn.msi.tropix.persistence.aop.PersistenceMethod;
import edu.umn.msi.tropix.persistence.aop.UserId;

public interface ProviderService {

  @PersistenceMethod
  Folder createNewProviderAndFolder(@UserId @Admin String adminId, final Folder folder, @AutoUser String ownerId);

  // Do the same for user
  @PersistenceMethod
  void addGroupToObjectsProvider(@UserId @Admin String adminId, final String objectId, final String groupId);

  @PersistenceMethod
  void addUserToObjectsProvider(@UserId @Admin String adminId, final String objectId, @AutoUser final String userId);

}
