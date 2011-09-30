package edu.umn.msi.tropix.proteomics.test;

import org.globus.exec.generated.JobDescriptionType;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorFactoryImpl;
import edu.umn.msi.tropix.common.jobqueue.test.JobProcessorFactoryTest;
import edu.umn.msi.tropix.proteomics.identification.IdentificationJobProcessorImpl;

public class IdentificationJobProcessorFactoryTest<U, T extends BaseExecutableJobProcessorFactoryImpl<? extends IdentificationJobProcessorImpl<U>>>
    extends JobProcessorFactoryTest<T> {

  private InputContext databaseContext;
  private InputContext mzxmlContext;
  private U parameters;
  private IdentificationJobProcessorImpl<U> jobProcessor;

  private ExecutableJobDescription description;
  private JobDescriptionType jobDescriptionType;

  protected ExecutableJobDescription getExecutableDescription() {
    return description;
  }

  protected JobDescriptionType getJobDescriptionType() {
    return jobDescriptionType;
  }

  protected void replayBuildJobProcessorPreprocessAndVerify() {
    getStagingDirectory().setup();
    replay();
    jobProcessor = getFactory().create(getJobProcessorConfiguration());
    jobProcessor.setDatabase(databaseContext);
    jobProcessor.setInputMzXML(mzxmlContext);
    jobProcessor.setInputParameters(getParameters());
    description = jobProcessor.preprocess();
    jobDescriptionType = description.getJobDescriptionType();
    verifyAndReset();
  }

  protected void replayPostprocessAndVerify() {
    getStagingDirectory().cleanUp();
    replay();
    jobProcessor.postprocess(true);
    verifyAndReset();
  }

  protected IdentificationJobProcessorImpl<U> getJobProcessor() {
    return jobProcessor;
  }

  protected void setParameters(final U parameters) {
    this.parameters = parameters;
  }

  protected U getParameters() {
    return parameters;
  }

  public InputContext getDatabaseContext() {
    return databaseContext;
  }

  public InputContext getMzxmlContext() {
    return mzxmlContext;
  }

  protected void expectDownloadDatabaseAndInput() {
    databaseContext = getDownloadContext();
    mzxmlContext = getDownloadContext();
  }

  protected void expectDownloadInputTo(final String inputPath) {
    getMzxmlContext().get(getDirMockOutputContext(inputPath));
  }

  protected void expectDownloadDatabaseTo(final String databasePath) {
    getDatabaseContext().get(getDirMockOutputContext(databasePath));
  }

}