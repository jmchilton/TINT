package edu.umn.msi.tropix.proteomics.scaffold.impl;

import edu.umn.msi.tropix.common.jobqueue.deployer.SingleScriptDeployerImpl;

public class FakeScaffoldDeployerImpl extends SingleScriptDeployerImpl {
  private static final String FAKE_SCAFFOLD_SCRIPT_NAME = "fake_scaffold.py";

  protected String getResourceName() {
    return FAKE_SCAFFOLD_SCRIPT_NAME;
  }

}
