package edu.umn.msi.tropix.webgui.functional;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.tools.ant.util.DateUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import edu.umn.msi.tropix.common.data.Repositories;
import edu.umn.msi.tropix.common.data.Repository;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;
import edu.umn.msi.tropix.webgui.client.constants.DomConstants;

public class Qc1FunctionalTest extends FunctionalTestBase {
  private static final String WORKFLOW_MENU_OPTION = "Integrated Identification Workflow";
  protected Date date = new Date();
  @Inject
  private Repository repository = Repositories.getInstance();

  @Test(groups = "functional")
  public void qaTest() {
    login();
    createFolders("QC", "Test1", DateUtils.format(date, DateUtils.ISO8601_DATETIME_PATTERN));
    
    final InputStream inputStream = repository.getResource(Qc1FunctionalTest.class, "HUMAN.fasta");
    uploadDatabase("HUMAN", Lists.newArrayList("My Home", "QC", "Test1", DateUtils.format(date, DateUtils.ISO8601_DATETIME_PATTERN)), inputStream);
    waitForDatabase();
    launchWorkflow();
    
    waitForNextStepComplete(); // Upload
    waitForNextStepComplete(); // Convert peak list
    waitForNextStepComplete(); // Start sequest
    waitForNextStepComplete(); // scaffold
    
  }  
  
  protected void waitForDatabase() {
    waitForNextStepComplete(); // Upload
    waitForNextStepComplete(); // Create database
  }
  
  private void launchWorkflow() {
    clickNewMenuOption(WORKFLOW_MENU_OPTION);
    wizardIdContext.next();
    waitForWizardNext();
    wizardNext();

    
    final File tempFile = getTempFieWithContents(".RAW", repository.getResource(Qc1FunctionalTest.class, "mrr.RAW"));
    wizardUpload(tempFile);
    
    wizardNext();
    
    metadataIdContext.next();
    final String treeId = DomConstants.buildConstant(DomConstants.METADATA_TREE_PREFIX, metadataIdContext.get());
    specifyObjectNameAs("workflow_output");
    selectHomeFolderAsDestination(treeId);
    //super.sleep(150);
    wizardNext();
    waitForAndClick("//nobr[text()=\"local://RawExtract\"]");    
    wizardNext();
    waitForAndClick("//nobr[text()=\"local://Sequest\"]");
    wizardNext();
        
    selectTreeItem("DB_SELECT_0", Lists.newArrayList("My Home", "QC", "Test1", DateUtils.format(date, DateUtils.ISO8601_DATETIME_PATTERN), "HUMAN"));
    super.sleep(250);
    wizardNext();
    super.sleep(350);
    wizardNext();
    super.sleep(350);
    wizardNext();
    super.sleep(350);
    wizardNext();
    super.sleep(350);
    wizardNext();
    super.sleep(350);
    wizardNext();
    super.sleep(350);
    wizardFinish();
    super.sleep(15000);
  }
    
}
