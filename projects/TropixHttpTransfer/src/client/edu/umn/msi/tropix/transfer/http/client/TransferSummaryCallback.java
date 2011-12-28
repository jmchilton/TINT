package edu.umn.msi.tropix.transfer.http.client;


public interface TransferSummaryCallback {
  
  void onTranfserComplete(final TransferSummary transferSummary);
  
}