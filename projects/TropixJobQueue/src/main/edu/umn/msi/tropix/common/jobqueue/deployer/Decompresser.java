package edu.umn.msi.tropix.common.jobqueue.deployer;

import java.io.File;

interface Decompresser {

  void decompress(final File compressedFile);
  
}
