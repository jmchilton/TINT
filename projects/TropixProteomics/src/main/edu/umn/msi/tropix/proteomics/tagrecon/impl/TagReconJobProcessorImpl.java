package edu.umn.msi.tropix.proteomics.tagrecon.impl;

import org.globus.exec.generated.JobDescriptionType;

import edu.umn.msi.tropix.proteomics.bumbershoot.BumbershootParameterUtils;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.TagParameters;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.TagReconParameters;
import edu.umn.msi.tropix.proteomics.identification.IdentificationJobProcessorImpl;

class TagReconJobProcessorImpl extends IdentificationJobProcessorImpl<TagParameters> {
  private static final String DATABASE_PATH = "db.fasta";
  private static final String MZXML_PATH = "input.mzXML";

  @Override
  protected void doPreprocessing() {

    getDatabase().get(getStagingDirectory().getOutputContext(DATABASE_PATH));
    getMzxml().get(getStagingDirectory().getOutputContext(MZXML_PATH));

    getStagingDirectory().getOutputContext("tagrecon.cfg").put(getTagReconConfigContents().getBytes());
    getStagingDirectory().getOutputContext("directag.cfg").put(getDirecTagConfigContents().getBytes());

    final JobDescriptionType jobDescriptionType = getJobDescription().getJobDescriptionType();
    jobDescriptionType.setDirectory(getStagingDirectory().getAbsolutePath());
    jobDescriptionType.setArgument(new String[] {"-ProteinDatabase", DATABASE_PATH, "input-tags.tags"});
  }

  private String getTagReconConfigContents() {
    final TagReconParameters parameters = getParameters().getTagReconParameters();
    return paramsToString(parameters);
  }

  private String getDirecTagConfigContents() {
    return paramsToString(getParameters().getDirecTagParameters());
  }

  private String paramsToString(final Object parameters) {
    String contents = "";
    if(parameters != null) {
      contents = BumbershootParameterUtils.serializeBumbershootParameters(parameters);
    }
    return contents;
  }

  @Override
  protected void doPostprocessing() {
    if(wasCompletedNormally()) {
      getResourceTracker().add(getStagingDirectory().getInputContext("input-tags.pepXML"));
    }
  }

}
