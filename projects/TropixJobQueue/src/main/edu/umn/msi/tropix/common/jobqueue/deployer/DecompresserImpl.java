package edu.umn.msi.tropix.common.jobqueue.deployer;

import java.io.File;

import edu.umn.msi.tropix.common.io.ZipUtils;
import edu.umn.msi.tropix.common.io.ZipUtilsFactory;

class DecompresserImpl implements Decompresser {
  private static final ZipUtils ZIP_UTILS = ZipUtilsFactory.getInstance();
  private SimpleExecutor simpleExecutor = new DefaultSimpleExecutorImpl();

  void setSimpleExecutor(final SimpleExecutor simpleExecutor) {
    this.simpleExecutor = simpleExecutor;
  }
  
  public void untar(final File file) {
    String unzip = "";
    if(file.getPath().endsWith(".bz2")) {
      unzip = "j";
    } else if(file.getPath().endsWith(".gz")) {
      unzip = "z";
    }
    simpleExecutor.executeInDirectory(file.getParentFile(), "/bin/tar", "xf" + unzip, file.getAbsolutePath());
  }

  public void unzip(final File file) {
    ZIP_UTILS.unzipToDirectory(file, file.getParentFile());
  }

  public void decompress(final File compressedFile) {
    if(compressedFile.getName().endsWith(".zip")) {
      unzip(compressedFile);
    } else {
      untar(compressedFile);
    }    
  }

}
