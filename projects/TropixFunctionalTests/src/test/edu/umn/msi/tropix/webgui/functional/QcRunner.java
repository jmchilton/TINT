package edu.umn.msi.tropix.webgui.functional;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.tools.ant.util.DateUtils;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.data.Repositories;
import edu.umn.msi.tropix.common.data.Repository;
import edu.umn.msi.tropix.webgui.client.constants.DomConstants;

public class QcRunner extends FunctionalTestBase {
  private static final String WORKFLOW_MENU_OPTION = "Integrated Identification Workflow";
  protected Date date = new Date();

  private Repository repository = Repositories.getInstance();

  public QcRunner() {
    super(TintInstance.STAGING);
  }
  
  public static void main(final String[] args) throws Exception {
    final QcRunner runner = new QcRunner();
    runner.setUp();
    try {
      runner.qaTest();
    } finally {
      runner.tearDown();
    }
  }
  
  @Test(groups = "functional")
  public void qaTest() {
    login();
    
    final String[] testPath = new String[] {"QC", "Test1", DateUtils.format(date, DateUtils.ISO8601_DATETIME_PATTERN)}; 
    createFolders(testPath);
    
    final InputStream inputStream = repository.getResource(QcRunner.class, "HUMAN.fasta");
    uploadDatabase("HUMAN", Lists.newArrayList("My Home", "QC", "Test1", DateUtils.format(date, DateUtils.ISO8601_DATETIME_PATTERN)), inputStream);
    waitForDatabase();
    launchWorkflow(Lists.asList("My Home", testPath));
    
    waitForNextStepComplete(); // Upload
    waitForNextStepComplete(); // Convert peak list
    waitForNextStepComplete(); // Start sequest
    waitForNextStepComplete(); // scaffold
    
  }  
  
  protected void waitForDatabase() {
    waitForNextStepComplete(); // Upload
    waitForNextStepComplete(); // Create database
  }
  
  private void launchWorkflow(final List<String> testPath) {
    clickNewMenuOption(WORKFLOW_MENU_OPTION);
    wizardIdContext.next();
    waitForWizardNext();
    wizardNext();

    
    final File tempFile = getTempFieWithContents(".RAW", repository.getResource(QcRunner.class, "mrr.RAW"));
    wizardUpload(tempFile);
    
    wizardNext();
    
    metadataIdContext.next();
    final String treeId = DomConstants.buildConstant(DomConstants.METADATA_TREE_PREFIX, metadataIdContext.get());
    specifyObjectNameAs("workflow_output");
    selectTreeItem(treeId, Lists.newArrayList(testPath));
    wizardNext();
    clickListGridText("local://RawExtract");
    wizardNext();
    clickListGridText("local://Sequest");
    wizardNext();
        
    selectTreeItem("DB_SELECT_0", Lists.newArrayList("My Home", "QC", "Test1", DateUtils.format(date, DateUtils.ISO8601_DATETIME_PATTERN), "HUMAN"));
    super.sleep(1000);
    wizardNext();
    super.sleep(1000);
    wizardNext();
    super.sleep(1000);
    wizardNext();
    super.sleep(1000);
    wizardNext();
    super.sleep(1000);
    wizardFinish();
  }
    
}
