package edu.umn.msi.tropix.jobs.newfile;

import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.file.NewFileMessageQueue.NewFileMessage;

public interface NewFileProcessor {

  void processFile(final NewFileMessage message, final TropixFile tropixFile);
  
}
