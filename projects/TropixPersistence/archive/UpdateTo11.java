package edu.umn.msi.tropix.persistence.service.impl;

import java.util.Collection;
import java.util.List;

import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.IdentificationParameters;
import edu.umn.msi.tropix.models.Sample;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.models.ThermofinniganRun;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.persistence.dao.TropixObjectDao;
import edu.umn.msi.tropix.persistence.dao.hibernate.TropixPersistenceTemplate;

public class UpdateTo11 extends TropixPersistenceTemplate {
  protected TropixObjectDao tropixObjectDao;
 
  @SuppressWarnings("unchecked")
  public void addData() {
    for(Folder folder : (List<Folder>) super.find("select * from Folder")) {
      if(folder.getParentFolder() != null) {
        tropixObjectDao.addPermissionParent(folder.getId(), folder.getParentFolder().getId());
      }
    }
    for(Database database : (List<Database>) super.find("select * from Database")) {
      tropixObjectDao.addPermissionParent(database.getId(), database.getParentFolder().getId());
      tropixObjectDao.addPermissionParent(database.getDatabaseFile().getId(), database.getId());
    }
    for(Sample sample : (List<Sample>) super.find("select * from Sample")) {
      tropixObjectDao.addPermissionParent(sample.getId(), sample.getParentFolder().getId());
    }
    for(ThermofinniganRun run : (List<ThermofinniganRun>) super.find("select * from ThermofinniganRun")) {
      ensurePermissionParent(run, run.getParentFolder());
      ensurePermissionParent(run.getRawFile(), run);
      ensurePermissionParent(run.getMzxml(), run);
    }
    for(IdentificationAnalysis analysis : (List<IdentificationAnalysis>) find("select * from IdentificationAnalysis")) {
      ensurePermissionParent(analysis, analysis.getParentFolder());
      ensurePermissionParent(analysis.getOutput(), analysis);
      ensurePermissionParent(analysis.getParameters(), analysis);
    }
    for(ScaffoldAnalysis analysis : (List<ScaffoldAnalysis>) find("select * from ScaffoldAnalysis")) {
      ensurePermissionParent(analysis, analysis.getParentFolder());
      ensurePermissionParent(analysis.getOutputs(), analysis);
      ensurePermissionParent(analysis.getInput(), analysis);
    }
    for(IdentificationParameters parameters : (List<IdentificationParameters>) super.find("select * from IdentificationParameters")) {
      if(parameters.getParentFolder() != null) {
        ensurePermissionParent(parameters, parameters.getParentFolder());
      }
    }    
    for(TropixObject object : (Collection<TropixObject>) find("select * from TropixObject")) {
      if(tropixObjectDao.getOwner(object.getId()) == null) {
        TropixObject parent = object.getPermissionParents().iterator().next();
        tropixObjectDao.setOwner(object.getId(), tropixObjectDao.getOwner(parent.getId()));
      }
    }
  }

  public void ensurePermissionParent(TropixObject child, TropixObject parent) {
    tropixObjectDao.addPermissionParent(child.getId(), parent.getId());
  }
  
}
