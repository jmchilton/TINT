package edu.umn.msi.tropix.persistence.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.message.MessageSource;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.IdentificationParameters;
import edu.umn.msi.tropix.models.Sample;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.models.ThermofinniganRun;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.dao.TropixObjectDao;
import edu.umn.msi.tropix.persistence.dao.UserDao;
import edu.umn.msi.tropix.persistence.dao.hibernate.TropixPersistenceTemplate;
import edu.umn.msi.tropix.persistence.service.UpdateService;
import edu.umn.msi.tropix.persistence.service.VerifyService;
import edu.umn.msi.tropix.persistence.service.impl.utils.ModelUtils;

public class UpdateTo12 extends TropixPersistenceTemplate implements UpdateService, VerifyService {
  
  @Autowired
  protected TropixObjectDao tropixObjectDao;
  @Autowired
  protected UserDao userDao;
  @Autowired
  protected MessageSource messageSource;
  
  private String fileTimeFile = System.getProperty("user.home") + File.separator + "filetime.properties";
  private Map<String,String> timeMap;

  @SuppressWarnings("unchecked")
  private void commitDatabaseFiles() {
    for(Database database : (List<Database>) super.find("from Database")) {
      database.setCommitted(true);
      tropixObjectDao.saveOrUpdateTropixObject(database);
    }
  }

  @SuppressWarnings("unchecked")
  private void createEveryoneGroup() {
    for(User user : (List<User>) super.find("from User")) {
      userDao.addToGroupWithName(user.getCagridId(), "*ALL USERS*");
      userDao.saveOrUpdateUser(user);
    }
  }

  @SuppressWarnings("unchecked")
  private void verifyMetadata() {
    for(ThermofinniganRun run : (List<ThermofinniganRun>) super.find("from ThermofinniganRun")) {
      TropixFile mzxmlFile = run.getMzxml();
      if(mzxmlFile != null) {
        assert mzxmlFile.getName().equals(messageSource.getMessage(MessageConstants.PROTEOMICS_RUN_MZXML_NAME, run.getName()));
        assert mzxmlFile.getDescription().trim().equals(messageSource.getMessage(MessageConstants.PROTEOMICS_RUN_MZXML_DESCRIPTION, run.getName()).trim()) : "Expected [" + messageSource.getMessage(MessageConstants.PROTEOMICS_RUN_MZXML_DESCRIPTION, run.getName()) + "] Found [" + mzxmlFile.getDescription() + "]";
        assert mzxmlFile.getType().equals(messageSource.getMessage(MessageConstants.FILE_TYPE_MZXML));
      }

      TropixFile  rawFile = run.getRawFile();
      assert rawFile.getName().equals(messageSource.getMessage(MessageConstants.THERMOFINNIGAN_RUN_RAW_NAME, run.getName()));
      assert rawFile.getDescription().trim().equals(messageSource.getMessage(MessageConstants.THERMOFINNIGAN_RUN_RAW_DESCRIPTION, run.getName()).trim());
      assert rawFile.getType().equals(messageSource.getMessage(MessageConstants.FILE_TYPE_THERMO_RAW));
    }
    for(Database database : (List<Database>) super.find("from Database")) {
      TropixFile fastaFile = database.getDatabaseFile();
      assert fastaFile.getName().equals(messageSource.getMessage(MessageConstants.DATABASE_FILE_NAME, database.getName()));
      assert fastaFile.getDescription().equals(messageSource.getMessage(MessageConstants.DATABASE_FILE_DESCRIPTION, database.getName()));
      assert fastaFile.getType().equals(messageSource.getMessage(MessageConstants.FILE_TYPE_FATSA));
    }
    for(IdentificationAnalysis analysis : (List<IdentificationAnalysis>) find("from IdentificationAnalysis")) {      
      String identificationType = analysis.getIdentificationProgram();
      String identificationFileType = getIdentificationFileType(identificationType);
      String extension = ModelUtils.getExtension(identificationFileType);

      IdentificationParameters parameters = analysis.getParameters();
      assert parameters.getName().equals(messageSource.getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_PARAMS_NAME, analysis.getName()));
      assert parameters.getDescription().trim().equals(messageSource.getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_PARAMS_DESCRIPTION, analysis.getName()).trim());
      assert parameters.getType().equals(analysis.getIdentificationProgram());

      TropixFile outputFile = analysis.getOutput();
      if(outputFile != null) {
        assert outputFile.getName().equals(messageSource.getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_OUTPUT_NAME, analysis.getName(), extension));
        assert outputFile.getDescription().trim().equals(messageSource.getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_OUTPUT_DESCRIPTION, analysis.getName()).trim());      
        assert outputFile.getType().equals(getIdentificationFileType(analysis.getIdentificationProgram()));
      }
    }
    for(ScaffoldAnalysis analysis : (List<ScaffoldAnalysis>) find("from ScaffoldAnalysis")) {
      TropixFile inputFile = analysis.getInput();
      assert inputFile.getName().equals(messageSource.getMessage(MessageConstants.SCAFFOLD_ANALYSIS_INPUT_NAME, analysis.getName()));
      assert inputFile.getDescription().equals(messageSource.getMessage(MessageConstants.SCAFFOLD_ANALYSIS_INPUT_DESCRIPTION, analysis.getName()));
      assert inputFile.getType().equals(messageSource.getMessage(MessageConstants.FILE_TYPE_SCAFFOLD_INPUT));

      TropixFile outputFile = analysis.getOutputs();
      if(outputFile != null) {
        assert outputFile.getName().equals(messageSource.getMessage(MessageConstants.SCAFFOLD_ANALYSIS_OUTPUT_NAME, analysis.getName()));
        assert outputFile.getDescription().equals(messageSource.getMessage(MessageConstants.SCAFFOLD_ANALYSIS_OUTPUT_DESCRIPTION, analysis.getName()));
        assert outputFile.getType().equals(messageSource.getMessage(MessageConstants.FILE_TYPE_SCAFFOLD_OUTPUT));
      }
    }

  }



  private String getIdentificationFileType(String identificationType) {
    String type = null;
    if(identificationType.equals("SequestBean")) {
      type = messageSource.getMessage(MessageConstants.FILE_TYPE_SEQUEST_OUTPUT);
    } else if(identificationType.equals("XTandemBean")) {
      type = messageSource.getMessage(MessageConstants.FILE_TYPE_XTANDEM_OUTPUT);
    } else if(identificationType.equals("OmssaXml")) {
      type = messageSource.getMessage(MessageConstants.FILE_TYPE_OMSSA_OUTPUT);
    } else if(identificationType.equals("Mascot")) {
      type = messageSource.getMessage(MessageConstants.FILE_TYPE_MASCOT_OUTPUT);
    } else {
      throw new IllegalStateException("Unknown identification analysis type (" + identificationType + ")");
    }
    return type;
  }

  private String getTime(TropixFile file) {
    if(file != null && file.getFileId() != null && timeMap.containsKey(file.getFileId())) {
      String seconds = timeMap.get(file.getFileId());
      return "" + Integer.parseInt(seconds) * 1000l; // Convert to useconds since epoch
    } else {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private void fillInMetadata() throws FileNotFoundException, IOException {
    Properties properties = new Properties();
    FileInputStream inputStream = new FileInputStream(new File(fileTimeFile));
    try {
      properties.load(inputStream);
      timeMap = Maps.fromProperties(properties);
    } finally {
      inputStream.close();
    }

    for(IdentificationAnalysis analysis : (List<IdentificationAnalysis>) find("from IdentificationAnalysis")) {
      String identificationType = analysis.getIdentificationProgram();

      // ID_ANALYSIS.IDENTIFICATION_PROGRAM is filled throughout, ID_PARAMERS.TYPE is not!
      TropixFile outputFile = analysis.getOutput();
      String time = getTime(outputFile);
      
      if(outputFile != null) {
        String identificationFileType = getIdentificationFileType(identificationType);
        String extension = ModelUtils.getExtension(identificationFileType);
        System.out.println(analysis.getName());
        outputFile.setName(messageSource.getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_OUTPUT_NAME, analysis.getName(), extension));
        outputFile.setDescription(messageSource.getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_OUTPUT_DESCRIPTION, analysis.getName()));
        outputFile.setType(identificationFileType);
        outputFile.setCreationTime(time);
        tropixObjectDao.saveOrUpdateTropixObject(outputFile);    
      }

      analysis.setCreationTime(time);
      tropixObjectDao.saveOrUpdateTropixObject(analysis);
      
      IdentificationParameters parameters = analysis.getParameters();
      parameters.setName(messageSource.getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_PARAMS_NAME, analysis.getName()));
      parameters.setDescription(messageSource.getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_PARAMS_DESCRIPTION, analysis.getName()));
      parameters.setType(identificationType);
      parameters.setCreationTime(time);
      tropixObjectDao.saveOrUpdateTropixObject(parameters);

    }
    
    for(Database database : (List<Database>) super.find("from Database")) {
      TropixFile fastaFile = database.getDatabaseFile();
      fastaFile.setName(messageSource.getMessage(MessageConstants.DATABASE_FILE_NAME, database.getName()));
      fastaFile.setDescription(messageSource.getMessage(MessageConstants.DATABASE_FILE_DESCRIPTION, database.getName()));
      fastaFile.setType(messageSource.getMessage(MessageConstants.FILE_TYPE_FATSA));
      String time = getTime(fastaFile);
      fastaFile.setCreationTime(time);
      tropixObjectDao.saveOrUpdateTropixObject(fastaFile);

      database.setCreationTime(time);
      tropixObjectDao.saveOrUpdateTropixObject(database);
    }
    for(ThermofinniganRun run : (List<ThermofinniganRun>) super.find("from ThermofinniganRun")) {
      TropixFile  rawFile = run.getRawFile();
      rawFile.setName(messageSource.getMessage(MessageConstants.THERMOFINNIGAN_RUN_RAW_NAME, run.getName()));
      rawFile.setDescription(messageSource.getMessage(MessageConstants.THERMOFINNIGAN_RUN_RAW_DESCRIPTION, run.getName()));
      rawFile.setType(messageSource.getMessage(MessageConstants.FILE_TYPE_THERMO_RAW));
      String time = getTime(rawFile);
      rawFile.setCreationTime(time);
      tropixObjectDao.saveOrUpdateTropixObject(rawFile);

      run.setCreationTime(time);
      tropixObjectDao.saveOrUpdateTropixObject(rawFile);

      TropixFile mzxmlFile = run.getMzxml();
      if(mzxmlFile != null) {
        mzxmlFile.setName(messageSource.getMessage(MessageConstants.PROTEOMICS_RUN_MZXML_NAME, run.getName()));
        mzxmlFile.setDescription(messageSource.getMessage(MessageConstants.PROTEOMICS_RUN_MZXML_DESCRIPTION, run.getName()));
        mzxmlFile.setType(messageSource.getMessage(MessageConstants.FILE_TYPE_MZXML));
        mzxmlFile.setCreationTime(getTime(mzxmlFile));
        tropixObjectDao.saveOrUpdateTropixObject(mzxmlFile);
      }
    }


    for(ScaffoldAnalysis analysis : (List<ScaffoldAnalysis>) find("from ScaffoldAnalysis")) {
      TropixFile inputFile = analysis.getInput();
      inputFile.setName(messageSource.getMessage(MessageConstants.SCAFFOLD_ANALYSIS_INPUT_NAME, analysis.getName()));
      inputFile.setDescription(messageSource.getMessage(MessageConstants.SCAFFOLD_ANALYSIS_INPUT_DESCRIPTION, analysis.getName()));
      inputFile.setType(messageSource.getMessage(MessageConstants.FILE_TYPE_SCAFFOLD_INPUT));
      String time = getTime(inputFile);
      inputFile.setCreationTime(time);
      tropixObjectDao.saveOrUpdateTropixObject(inputFile);

      analysis.setCreationTime(time);
      tropixObjectDao.saveOrUpdateTropixObject(analysis);
      
      TropixFile outputFile = analysis.getOutputs();
      if(outputFile != null) {
        outputFile.setName(messageSource.getMessage(MessageConstants.SCAFFOLD_ANALYSIS_OUTPUT_NAME, analysis.getName()));
        outputFile.setDescription(messageSource.getMessage(MessageConstants.SCAFFOLD_ANALYSIS_OUTPUT_DESCRIPTION, analysis.getName()));
        outputFile.setType(messageSource.getMessage(MessageConstants.FILE_TYPE_SCAFFOLD_OUTPUT));
        outputFile.setCreationTime(getTime(outputFile));
        tropixObjectDao.saveOrUpdateTropixObject(outputFile);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void verify() {
    
    verifyMetadata();
    verifyPermissionParents();
    for(TropixObject object : (List<TropixObject>) super.find("from TropixObject")) {
      assert object.getDeletedTime() == null;
    }
    
  }
  
  @SuppressWarnings("unchecked")
  public void verifyPermissionParents() {
    List<User> users = super.find("from User");
    List<String> homeFolderIds = new ArrayList<String>(users.size());
    for(User user : users) {
      homeFolderIds.add(user.getHomeFolder().getId());
    }
    List<TropixObject> objects = super.find("from TropixObject");
    for(TropixObject object : objects) {
      if(object.getPermissionParents().size() != 1) {
        assert homeFolderIds.contains(object.getId()) : "Non home folder has no permission parents " + object.getId();
      } 
      assert tropixObjectDao.getOwner(object.getId()) != null : "Object " + object.getId() + " has no owner";
    }
    for(Database database : (List<Database>) super.find("from Database")) {
      assert database.getParentFolder() != null;
      assert database.getPermissionParents().iterator().next().equals(database.getParentFolder());
      assert database.getDatabaseFile() != null;
      assert database.getDatabaseFile().getPermissionParents().iterator().next().equals(database);
    }
    for(Sample sample : (List<Sample>) super.find("from Sample")) {
      assert sample.getParentFolder() != null;
      assert sample.getPermissionParents().iterator().next().equals(sample.getParentFolder());
    }
    for(ThermofinniganRun run : (List<ThermofinniganRun>) super.find("from ThermofinniganRun")) {
      assert run.getParentFolder() != null;
      assert run.getPermissionParents().iterator().next().equals(run.getParentFolder());
      assert run.getRawFile().getPermissionParents().iterator().next().equals(run);
      assert run.getMzxml().getPermissionParents().iterator().next().equals(run);
    }
    Set<String> analysisParameterIds = new TreeSet<String>();
    for(IdentificationAnalysis analysis : (List<IdentificationAnalysis>) super.find("from IdentificationAnalysis")) {
      assert analysis.getRun() != null;
      assert analysis.getPermissionParents().iterator().next().equals(analysis.getParentFolder());
      assert analysis.getOutput().getPermissionParents().iterator().next().equals(analysis);
      assert analysis.getParameters().getPermissionParents().iterator().next().equals(analysis);
      analysisParameterIds.add(analysis.getParameters().getId());
    }
    for(ScaffoldAnalysis analysis : (List<ScaffoldAnalysis>) super.find("from ScaffoldAnalysis")) {
      assert analysis.getInput() != null;
      assert analysis.getOutputs() != null;
      assert analysis.getPermissionParents().iterator().next().equals(analysis.getParentFolder());
      assert analysis.getOutputs().getPermissionParents().iterator().next().equals(analysis);
      assert analysis.getInput().getPermissionParents().iterator().next().equals(analysis);
    }
    for(IdentificationParameters parameters : (List<IdentificationParameters>) super.find("from IdentificationParameters")) {
      if(parameters.getParentFolder() != null) {
        assert parameters.getPermissionParents().iterator().next().equals(parameters.getParentFolder());
      } else {
        assert analysisParameterIds.contains(parameters.getId());
      }
    }
  }
 

  public void update() {
    try {
      commitDatabaseFiles();
      createEveryoneGroup();
      fillInMetadata();
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
}
