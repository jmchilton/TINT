fpackage edu.umn.msi.tropix.storage.client.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RandomFileWriter {
  public static void writeRandomFile(final File file, final long numKBytes) throws IOException {
    final Random random = new Random();
    final byte[] randomKiloByte = new byte[1024];
    final FileWriter fileWriter = new FileWriter(file);
    try {
      for(int i = 0; i < numKBytes; i++) {
        random.nextBytes(randomKiloByte);
        fileWriter.write(new String(randomKiloByte));
      }
    } finally {
      fileWriter.flush();
      fileWriter.close();
    }
  }

}
