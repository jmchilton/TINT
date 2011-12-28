package edu.umn.msi.tropix.transfer.http.client;

public interface TransferSummary {
  
  long getBytesTransferred();
  
  byte[] getMd5Sum();
}