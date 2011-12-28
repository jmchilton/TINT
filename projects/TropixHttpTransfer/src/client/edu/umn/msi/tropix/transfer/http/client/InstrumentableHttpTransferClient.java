package edu.umn.msi.tropix.transfer.http.client;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;

public interface InstrumentableHttpTransferClient extends HttpTransferClient {

  InputContext getInputContext(final String url, final TransferSummaryCallback callback);

  OutputContext getOutputContext(final String url, final TransferSummaryCallback callback);

}
