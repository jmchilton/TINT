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
  protected String base = "file123base";
  protected final String path = "/moo/cow/path";
  protected final String params = "test params";
  protected RawExtractJobFactoryImpl factory;
  protected CredentialedStagingDirectoryFactory stagingDirectoryFactory;
  protected StagingDirectory stagingDirectory;
  protected DisposableResourceTracker tracker;
  protected InputContext rawPopulator;
  protected DTAToMzXMLConverter converter;
  protected DTAToMzXMLOptions options;
  protected final Credential proxy = Credentials.getMock();
  protected MockObjectCollection mockObjects;
  protected RawExtractJobProcessorImpl job;

  // Test specific fields
  protected boolean doPreprocessing;
  protected boolean completedNormally;

  public BaseRawExtractJobFactoryImplTest() {
    super();
  }

  @BeforeMethod(groups = "unit")
  public void init() {
    factory = new RawExtractJobFactoryImpl();
    stagingDirectoryFactory = EasyMock.createMock(CredentialedStagingDirectoryFactory.class);
    stagingDirectory = EasyMock.createMock(StagingDirectory.class);
    EasyMock.expect(stagingDirectory.getAbsolutePath()).andStubReturn(path);

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
      EasyMock.expect(stagingDirectoryFactory.get(proxy)).andReturn(stagingDirectory);
      stagingDirectory.setup();
      final OutputContext rawContext = EasyMock.createMock(OutputContext.class);
      EasyMock.expect(stagingDirectory.getOutputContext(String.format("%s.RAW", base))).andReturn(rawContext);
      rawPopulator.get(rawContext);
    } else {
      EasyMock.expect(stagingDirectoryFactory.get(proxy, path)).andReturn(stagingDirectory);
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
    final JobProcessorConfiguration configuration = JobProcessorConfigurationFactories.getInstance().get(proxy);
    job = factory.buildJob(configuration, rawPopulator, params, base);
    final ExecutableJobDescription outputDescription = job.preprocess();
    return outputDescription;
  }
}