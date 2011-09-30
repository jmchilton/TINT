package edu.umn.msi.tropix.persistence.service.impl;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.collect.Sets;

import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.models.GalaxyToolRevision;
import edu.umn.msi.tropix.persistence.dao.Dao;
import edu.umn.msi.tropix.persistence.dao.DaoFactory;
import edu.umn.msi.tropix.persistence.dao.GalaxyToolRevisionDao;
import edu.umn.msi.tropix.persistence.galaxy.PersistentGalaxyToolStore;

@ManagedBean
class PersistentGalaxyToolStoreImpl implements PersistentGalaxyToolStore {
  private final Dao<GalaxyTool> galaxyToolDao;
  private final GalaxyToolRevisionDao galaxyToolRevisionDao;
  
  @Inject
  PersistentGalaxyToolStoreImpl(final GalaxyToolRevisionDao dao, final DaoFactory daoFactory) {
    this.galaxyToolRevisionDao = dao;
    this.galaxyToolDao = daoFactory.getDao(GalaxyTool.class);
  }
  
  public GalaxyTool create(final GalaxyTool galaxyTool, final String xml) {
    galaxyToolDao.saveObject(galaxyTool);
    GalaxyToolRevision revision = new GalaxyToolRevision();
    revision.setRevisionNum(1L);
    revision.setXml(xml);
    revision.setTool(galaxyTool);
    galaxyTool.setRevisions(Sets.newHashSet(revision));
    galaxyToolRevisionDao.saveObject(revision);
    return galaxyTool;
  }

  public String getXml(final String toolId) {
    GalaxyToolRevision revision = galaxyToolRevisionDao.getLatestRevision(toolId);
    return revision.getXml();
  }

  public List<GalaxyTool> list() {
    return galaxyToolDao.findAll();
  }

  public synchronized void updateIfNeeded(final GalaxyTool galaxyTool, final String updatedXml) {
    final String currentXml = getXml(galaxyTool.getId());    
    if(currentXml.equals(updatedXml)) {
      return;
    }
    update(galaxyTool, updatedXml);
  }
  
  public synchronized void update(final GalaxyTool galaxyTool, final String updatedXml) {
    final GalaxyToolRevision lastRevision = galaxyToolRevisionDao.getLatestRevision(galaxyTool.getId());
    final GalaxyTool tool = galaxyToolDao.load(galaxyTool.getId());
    tool.setName(galaxyTool.getName());
    tool.setDescription(galaxyTool.getDescription());
    galaxyToolDao.mergeEntity(tool);
    final GalaxyToolRevision newRevision = new GalaxyToolRevision();
    newRevision.setXml(updatedXml);
    newRevision.setTool(tool);
    newRevision.setRevisionNum(lastRevision.getRevisionNum()+1);
    galaxyToolRevisionDao.saveObject(newRevision);
    tool.getRevisions().add(newRevision);
  }

  public GalaxyTool load(final String toolId) {
    return galaxyToolDao.load(toolId);
  }

}
