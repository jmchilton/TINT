package edu.umn.msi.tropix.proteomics.sequest.impl;

import edu.umn.msi.tropix.common.jobqueue.deployer.SingleScriptDeployerImpl;

public class FakeSequestDeployerImpl extends SingleScriptDeployerImpl {
  private static final String FAKE_SEQUEST_SCRIPT_NAME = "fake_sequest.py";

  protected String getResourceName() {
    return FAKE_SEQUEST_SCRIPT_NAME;
  }
}
