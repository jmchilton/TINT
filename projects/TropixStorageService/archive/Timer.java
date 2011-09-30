package edu.umn.msi.tropix.storage.client.test;

import java.io.File;
import java.util.UUID;

import edu.umn.msi.tropix.storage.client.TropixStorageClient;

public class Timer {
  public static long timeRandomUpload(final TropixStorageClient client, final long numKBytes) throws Exception {
    final File tempFile = File.createTempFile("time", "");
    RandomFileWriter.writeRandomFile(tempFile, numKBytes);
    final UUID randomUUID = UUID.randomUUID();
    final String id = randomUUID.toString();
    final long start = System.currentTimeMillis();
    client.upload(id, tempFile);
    final long end = System.currentTimeMillis();
    client.delete(id);
    return end - start;
  }

  public static long timeRandomDownload(final TropixStorageClient client, final long numKBytes) throws Exception {
    final File tempFile = File.createTempFile("time", "");
    RandomFileWriter.writeRandomFile(tempFile, numKBytes);
    final UUID randomUUID = UUID.randomUUID();
    final String id = randomUUID.toString();
    client.upload(id, tempFile);
    final long start = System.currentTimeMillis();
    client.download(id);
    final long end = System.currentTimeMillis();
    client.delete(id);
    return end - start;
  }

  public static long[] timeRandomDownload(final TropixStorageClient client, final long[] numKBytes) throws Exception {
    final long[] times = new long[numKBytes.length];
    for(int i = 0; i < times.length; i++) {
      times[i] = timeRandomDownload(client, numKBytes[i]);
    }
    return times;
  }

  public static long[] timeRandomUpload(final TropixStorageClient client, final long[] numKBytes) throws Exception {
    final long[] times = new long[numKBytes.length];
    for(int i = 0; i < times.length; i++) {
      times[i] = timeRandomUpload(client, numKBytes[i]);
    }
    return times;
  }

}
