package edu.umn.msi.tropix.proteomics.test;

import java.math.BigInteger;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.test.BaseFileJobQueueContextImplTest;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.models.sequest.cagrid.SequestParameters;
import edu.umn.msi.tropix.models.xtandem.cagrid.XTandemParameters;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.MyriMatchParameters;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.TagReconParameters;
import edu.umn.msi.tropix.proteomics.identification.IdentificationJobProcessorBuilder;
import edu.umn.msi.tropix.proteomics.inspect.parameters.InspectParameters;
import edu.umn.msi.tropix.proteomics.service.impl.IdentificationJobQueueContextImpl;
import edu.umn.msi.tropix.proteomics.service.impl.InspectJobQueueContextImpl;
import edu.umn.msi.tropix.proteomics.service.impl.MyriMatchJobQueueContextImpl;
import edu.umn.msi.tropix.proteomics.service.impl.OmssaJobQueueContextImpl;
import edu.umn.msi.tropix.proteomics.service.impl.SequestJobQueueContextImpl;
import edu.umn.msi.tropix.proteomics.service.impl.TagReconJobQueueContextImpl;
import edu.umn.msi.tropix.proteomics.service.impl.XTandemJobQueueContextImpl;
import edu.umn.msi.tropix.transfer.types.TransferResource;
import gov.nih.nlm.ncbi.omssa.MSSearchSettings;

public class IdentificationJobQueueContextTests extends BaseFileJobQueueContextImplTest {
  private Capture<?> parameterCapture;
  private IdentificationJobProcessorBuilder<?> builder;
  private IdentificationJobQueueContextImpl<?, ?> context;
  private TransferResource mzxmlRef;
  private TransferResource dbRef;
  private InputContext dbContext;
  private InputContext mzxmlContext;

  @SuppressWarnings("unchecked")
  private <T> T expectParameters() {
    return (T) EasyMock.capture(parameterCapture);
  }

  @BeforeMethod(groups = "unit")
  public void initIdentificationFields() {
    parameterCapture = EasyMockUtils.newCapture();

    mzxmlRef = getReference();
    dbRef = getReference();
    dbContext = getDownloadContext(dbRef);
    mzxmlContext = getDownloadContext(mzxmlRef);

  }

  private <T> IdentificationJobProcessorBuilder<T> getMockIdentificationJobBuilder() {
    @SuppressWarnings("unchecked")
    final IdentificationJobProcessorBuilder<T> jobBuilder = EasyMock.createMock(IdentificationJobProcessorBuilder.class);
    addMock(jobBuilder);
    builder = jobBuilder;
    return jobBuilder;
  }

  private <T, U> void setContext(final IdentificationJobQueueContextImpl<T, U> context) {
    context.setIdentificationJobProcessorBuilder(this.<U>getMockIdentificationJobBuilder());
    init(context);
    this.context = context;
  }

  @SuppressWarnings("unchecked")
  private <T> IdentificationJobProcessorBuilder<T> getBuilder() {
    return (IdentificationJobProcessorBuilder<T>) builder;
  }

  @SuppressWarnings("unchecked")
  private <T, U> IdentificationJobQueueContextImpl<T, U> getContext() {
    return (IdentificationJobQueueContextImpl<T, U>) context;
  }

  @SuppressWarnings("unchecked")
  private <T> T capturedParameters() {
    return (T) parameterCapture.getValue();
  }

  @Test(groups = "unit")
  public void submitXTandem() {
    setContext(new XTandemJobQueueContextImpl());
    final XTandemParameters params = new XTandemParameters();
    params.setOutputHistogramColumnWidth(BigInteger.valueOf(23));
    replaySubmitAndVerify(params, "XTandem");
    assert this.<edu.umn.msi.tropix.models.xtandem.XTandemParameters>capturedParameters().getOutputHistogramColumnWidth() == 23;
  }

  private void replaySubmitAndVerify(final Object params, final String type) {
    getBuilder().buildJob(expectConfiguration(), EasyMock.same(mzxmlContext), expectParameters(), EasyMock.same(dbContext), EasyMock.eq("HUMAN.fasta"));
    expectLastCallAndReturnFileJobProcessor();
    expectSubmitJob(type);
    doReplay();
    getContext().submitJob(mzxmlRef, dbRef, getCredentialReference(), params, "HUMAN.fasta");
    doVerify();
  }

  @Test(groups = "unit")
  public void testMyriMatch() {
    setContext(new MyriMatchJobQueueContextImpl());
    final MyriMatchParameters parameters = new MyriMatchParameters();
    replaySubmitAndVerify(parameters, "MyriMatch");
    assert capturedParameters() == parameters;
  }

  @Test(groups = "unit")
  public void testTagRecon() {
    setContext(new TagReconJobQueueContextImpl());
    final TagReconParameters parameters = new TagReconParameters();
    replaySubmitAndVerify(parameters, "TagRecon");
    assert capturedParameters() == parameters;
  }

  @Test(groups = "unit")
  public void submitOmssa() {
    setContext(new OmssaJobQueueContextImpl());
    final MSSearchSettings params = new MSSearchSettings();
    replaySubmitAndVerify(params, "Omssa");
    assert capturedParameters() == params;
  }

  @Test(groups = "unit")
  public void submitInspect() {
    setContext(new InspectJobQueueContextImpl());
    final InspectParameters params = new InspectParameters();
    replaySubmitAndVerify(params, "Inspect");
    assert capturedParameters() == params;
  }

  @Test(groups = "unit")
  public void submitSequest() {
    setContext(new SequestJobQueueContextImpl());
    final SequestParameters params = new SequestParameters();
    params.setAddA(234.0);
    replaySubmitAndVerify(params, "Sequest");
    assert this.<edu.umn.msi.tropix.models.sequest.SequestParameters>capturedParameters().getAddA() == 234.0;
  }

}
