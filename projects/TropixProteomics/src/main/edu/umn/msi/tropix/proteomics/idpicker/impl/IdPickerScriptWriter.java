package edu.umn.msi.tropix.proteomics.idpicker.impl;

import java.io.File;
import java.util.regex.Matcher;

import javax.annotation.Nullable;

import edu.umn.msi.tropix.common.data.Repositories;
import edu.umn.msi.tropix.common.data.Repository;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.OutputContexts;

class IdPickerScriptWriter {
  private static final Repository REPOSITORY = Repositories.getInstance();
  
  public static File writeScript(final File parentDirectory, @Nullable final String idPickerHome) {
    final File scriptFile = new File(parentDirectory, "idpicker.bat");
    final String scriptContents = getScriptContents(idPickerHome);
    OutputContexts.forFile(scriptFile).put(scriptContents.getBytes());
    return scriptFile;
  }
  
  private static String addSeparator(final String path) {
    return path + (path.endsWith(File.separator) ? "" : File.separator);
  }
  
  private static String getScriptContents(@Nullable final String idPickerHome) {
    String idPickerPath = "";
    if(idPickerHome != null) {
      idPickerPath = addSeparator(idPickerHome);
    }
    
    String scriptContents = InputContexts.toString(REPOSITORY.getResourceContext(IdPickerScriptWriter.class, "idpicker.bat"));
    scriptContents = scriptContents.replaceAll("IDPICKER_PATH", Matcher.quoteReplacement(idPickerPath));
    return scriptContents;
  }
  
}
