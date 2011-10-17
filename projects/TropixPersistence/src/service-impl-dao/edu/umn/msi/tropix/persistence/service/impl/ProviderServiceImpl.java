package edu.umn.msi.tropix.persistence.service.impl;

import javax.annotation.ManagedBean;

import com.google.common.collect.Sets;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.Provider;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.dao.Dao;
import edu.umn.msi.tropix.persistence.service.ProviderService;

@ManagedBean
public class ProviderServiceImpl extends ServiceBase implements ProviderService {

  public Folder createNewProviderAndFolder(final String adminId, final Folder folder, final String ownerId) {
    final Provider provider = newProvider();
    provider.setObjects(Sets.<TropixObject>newHashSet());
    provider.setGroups(Sets.<Group>newHashSet());
    provider.setUsers(Sets.<User>newHashSet());
    saveNewObject(folder, ownerId);
    saveToProvider(provider, folder);
    return folder;
  }

  private Provider newProvider() {
    Dao<Provider> providerDao = getProviderDao();
    final Provider provider = new Provider();
    provider.setRole("write");
    providerDao.saveObject(provider);
    return provider;
  }

  public void addGroupToObjectsProvider(final String adminId, final String objectId, final String groupId) {
    final Provider provider = getProviderDao().getObjectsProvider(objectId);
    provider.getGroups().add(getDaoFactory().getDao(Group.class).load(groupId));
    getProviderDao().saveObject(provider);
  }

}
