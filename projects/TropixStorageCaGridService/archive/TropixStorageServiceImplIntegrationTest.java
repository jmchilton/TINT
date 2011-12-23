package edu.umn.msi.tropix.storage.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.io.IOUtils;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.Status;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.storage.service.TropixStorageService;


public class TropixStorageServiceImplIntegrationTest {
  FileWriter log;
  {
    try {
      log = new FileWriter(new File("/home/john/idlog"), true);
    } catch(IOException e1) {
      throw new RuntimeException();
    }
  }
  GlobusCredential proxy;
  {
    try {
      proxy = new GlobusCredential("/home/john/.tropix/client/host-cert.pem", "/home/john/.tropix/client/host-key.pem");
    } catch(GlobusCredentialException e) {
      throw new RuntimeException();
    }
  }
  final String service1Url = "https://chilton.msi.umn.edu:8082/wsrf/services/cagrid/TropixStorage";
  final String service2Url = "https://tint-storage.msi.umn.edu:8443/wsrf/services/cagrid/TropixStorage";

  @Test(groups = "integration")
  public void load() throws MalformedURIException, RemoteException, InterruptedException {
    int numThreads = 1;
    ArrayList<Thread> threads = new ArrayList<Thread>(numThreads);
    for(int i = 0; i < numThreads; i++) {
      Thread thread = new Thread(new WriterRunnable());
      thread.start();
      threads.add(thread);
    }
    for(Thread thread : threads) {
      thread.join();
    }
  }

  private synchronized void log(String id, boolean worked1, boolean worked2, int numBytes) {
    try {
      log.append(id + "\t" + worked1 + "\t" + worked2 + "\t" + numBytes + "\n");
      log.flush();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

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


  private class WriterRunnable implements Runnable {

    public void run() {
      File tempFile = null;
      try {
        try {
          tempFile = File.createTempFile("storagetest", "");
          writeRandomFile(tempFile, 1024 * 1);
        } catch(IOException e1) { 
          throw new RuntimeException(e1); 
        } 

        for(int i = 0; i < 100; i++) {
          String id = UUID.randomUUID().toString();
          // Random number of bytes from 10 kilo bytes to 10 megabytes
          boolean worked1 = false, worked2 = false;
          /*
          try {
            TropixStorageClientImpl client1 = new TropixStorageClientImpl(new TropixStorageInterfacesClient(service1Url, proxy), proxy);
            client1.upload(id, tempFile);
            Thread.sleep(3000);
            worked1 = client1.exists(id);
          } catch(Exception e) {
            e.printStackTrace();
          }
          */
          try {
            TropixStorageService client2 = null; //new TropixStorageClientImpl(new TropixStorageInterfacesClient(service2Url, proxy), proxy);
            upload(client2, id, tempFile);
            Thread.sleep(3000);
            worked2 = client2.exists(id);
          } catch(Exception e) { 
            e.printStackTrace();
          }
          log(id, worked1, worked2, 1024 * 100 * 1024);
        }
      } finally {
        tempFile.delete();
      }

    }

  }


  public void download(final TropixStorageService tropixStorageService, final String id, final File file) throws Exception {
    download(tropixStorageService, id, new FileOutputStream(file));
  }

  public byte[] download(final TropixStorageService tropixStorageService, final String id) throws Exception {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    download(tropixStorageService, id, outputStream);
    return outputStream.toByteArray();
  }

  public void upload(final TropixStorageService tropixStorageService, final String id, final File file) throws Exception {
    upload(tropixStorageService, id, new FileInputStream(file), file.length());
  }

  public void upload(final TropixStorageService tropixStorageService, final String id, final byte[] bytes) throws Exception {
    upload(tropixStorageService, id, new ByteArrayInputStream(bytes), bytes.length);
  }

  private void upload(final TropixStorageService tropixStorageService, final String id, final InputStream inputStream, final long length) throws Exception {
    final TransferServiceContextReference reference = tropixStorageService.prepareUpload(id);
    TransferServiceContextClient tclient;
    if(proxy == null) {
      tclient = new TransferServiceContextClient(reference.getEndpointReference());
      TransferClientHelper.putData(inputStream, length, tclient.getDataTransferDescriptor());
    } else {
      tclient = new TransferServiceContextClient(reference.getEndpointReference(), proxy);
      TransferClientHelper.putData(inputStream, length, tclient.getDataTransferDescriptor(), proxy);
    }
    inputStream.close();
    tclient.setStatus(Status.Staged);
  }

  
  
  private void download(final TropixStorageService tropixStorageService, final String id, final OutputStream outputStream) throws Exception {
    final TransferServiceContextReference reference = tropixStorageService.prepareDownload(id);
    TransferServiceContextClient tclient;
    InputStream inputStream;
    if(proxy == null) {
      tclient = new TransferServiceContextClient(reference.getEndpointReference());
      inputStream = TransferClientHelper.getData(tclient.getDataTransferDescriptor());
    } else {
      tclient = new TransferServiceContextClient(reference.getEndpointReference(), proxy);
      inputStream = TransferClientHelper.getData(tclient.getDataTransferDescriptor(), proxy);
    }
    IOUtils.copyLarge(inputStream, outputStream);
  }

}
