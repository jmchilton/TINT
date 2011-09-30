package edu.umn.msi.tropix.proteomics.idpicker.impl;

import java.io.File;

import org.springframework.util.StringUtils;

import edu.umn.msi.tropix.common.jobqueue.deployer.BaseDeployerImpl;

public class IdPickerScriptDeployerImpl extends BaseDeployerImpl {
  private String idPickerHome;
  
  protected void deploy() {
    if(!StringUtils.hasText(idPickerHome)) {
      deployIdPicker();
    }
    final File idPickerScript = IdPickerScriptWriter.writeScript(getDeploymentDirectory(), idPickerHome);
    makeExecutable(idPickerScript);
    addProperty(idPickerScript.getAbsolutePath());
  }

  private void deployIdPicker() {
    final File defaultIdPickerDirectory = getFile("idpicker");
    if(!defaultIdPickerDirectory.exists()) {
      defaultIdPickerDirectory.getParentFile().mkdirs();
      final String compressedIdPicker = "idpicker.zip";
      File compressedIdPickerFile = getFile(compressedIdPicker);
      copyResource(compressedIdPicker);
      decompress(compressedIdPickerFile);
    }
    this.idPickerHome = defaultIdPickerDirectory.getAbsolutePath();
  }
  
  public void setIdPickerHome(final String idPickerHome) {
    this.idPickerHome = idPickerHome;
  }

}
