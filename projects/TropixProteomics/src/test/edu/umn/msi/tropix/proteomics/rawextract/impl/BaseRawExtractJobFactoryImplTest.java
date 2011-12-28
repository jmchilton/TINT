package edu.umn.msi.tropix.proteomics.rawextract.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfigurationFactories;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.DisposableResourceTracker;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.io.CredentialedStagingDirectoryFactory;
import edu.umn.msi.tropix.proteomics.conversion.DTAToMzXMLConverter;
import edu.umn.msi.tropix.proteomics.conversion.DTAToMzXMLOptions;

public class BaseRawExtractJobFactoryImplTest {
  protected static final String BASE = "file123base";
  protected static final String PATH = "/moo/cow/path";
  protected static final String PARAMS = "test params";
  private RawExtractJobFactoryImpl factory;
  private CredentialedStagingDirectoryFactory stagingDirectoryFactory;
  private StagingDirectory stagingDirectory;
  public StagingDirectory getStagingDirectory() {
    return stagingDirectory;
  }
  
  protected RawExtractJobFactoryImpl getFactory() {
    return factory;
  }

  public void setStagingDirectory(final StagingDirectory stagingDirectory) {
    this.stagingDirectory = stagingDirectory;
  }

  private DisposableResourceTracker tracker;
  private InputContext rawPopulator;
  private DTAToMzXMLConverter converter;
  private DTAToMzXMLOptions options;
  protected static final Credential PROXY = Credentials.getMock();
  private MockObjectCollection mockObjects;
  private RawExtractJobProcessorImpl job;

  protected void setJob(final RawExtractJobProcessorImpl job) {
    this.job = job;
  }
  
  protected DisposableResourceTracker getTracker() {
    return tracker;
  }
  
  protected DTAToMzXMLConverter getConverter() {
    return converter;
  }
  
  // Test specific fields
  private boolean doPreprocessing;
  private boolean completedNormally;
  
  protected DTAToMzXMLOptions getOptions() {
    return options;
  }
  
  protected void setDoPreprocessing(final boolean doPreprocessing) {
    this.doPreprocessing = doPreprocessing;
  }

  protected void setCompletedNormally(final boolean completedNormally) {
    this.completedNormally = completedNormally;
  }
  
  protected boolean isCompletedNormally() {
    return completedNormally;
  }
  
  protected boolean getDoPreprocessing() {
    return doPreprocessing;
  }
  
  
  public BaseRawExtractJobFactoryImplTest() {
    super();
  }

  @BeforeMethod(groups = "unit")
  public void init() {
    factory = new RawExtractJobFactoryImpl();
    stagingDirectoryFactory = EasyMock.createMock(CredentialedStagingDirectoryFactory.class);
    stagingDirectory = EasyMock.createMock(StagingDirectory.class);
    EasyMock.expect(stagingDirectory.getAbsolutePath()).andStubReturn(PATH);

    EasyMock.expect(stagingDirectory.getSep()).andStubReturn("/");
    final Supplier<DisposableResourceTracker> trackerSupplier = EasyMockUtils.createMockSupplier();
    tracker = createMock(DisposableResourceTracker.class);
    expect(trackerSupplier.get()).andReturn(tracker);
    replay(trackerSupplier);
    rawPopulator = EasyMock.createMock(InputContext.class);

    converter = EasyMock.createMock(DTAToMzXMLConverter.class);
    options = new DTAToMzXMLOptions();

    factory.setDtaToMzXMLConverter(converter);
    factory.setDtaToMxXMLOptions(options);
    factory.setDisposableResourceTrackerSupplier(trackerSupplier);
    factory.setCredentialedStagingDirectoryFactory(stagingDirectoryFactory);
    mockObjects = MockObjectCollection.fromObjects(stagingDirectoryFactory, stagingDirectory, tracker, rawPopulator, converter);
  }

  protected void expectPreprocessingIfNeeded() {
    if(doPreprocessing) {
      EasyMock.expect(stagingDirectoryFactory.get(PROXY)).andReturn(stagingDirectory);
      stagingDirectory.setup();
      final OutputContext rawContext = EasyMock.createMock(OutputContext.class);
      EasyMock.expect(stagingDirectory.getOutputContext(String.format("%s.RAW", BASE))).andReturn(rawContext);
      rawPopulator.get(rawContext);
    } else {
      EasyMock.expect(stagingDirectoryFactory.get(PROXY, PATH)).andReturn(stagingDirectory);
    }
  }

  protected void expectCleanUp() {
    stagingDirectory.cleanUp();
  }

  protected void postProcessAndVerify() {
    try {
      job.postprocess(completedNormally);
    } finally {
      mockObjects.verifyAndReset();
    }
  }

  protected void expectPreprocessingAndReplayMocks() {
    expectPreprocessingIfNeeded();
    expectCleanUp();
    mockObjects.replay();
  }

  protected ExecutableJobDescription buildJobAndPreprocess() {
    final JobProcessorConfiguration configuration = JobProcessorConfigurationFactories.getInstance().get(PROXY);
    job = factory.buildJob(configuration, rawPopulator, PARAMS, BASE);
    final ExecutableJobDescription outputDescription = job.preprocess();
    return outputDescription;
  }
}