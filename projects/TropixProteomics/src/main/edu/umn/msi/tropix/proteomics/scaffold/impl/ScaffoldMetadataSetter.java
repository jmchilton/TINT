package edu.umn.msi.tropix.proteomics.scaffold.impl;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.grid.metadata.service.MetadataBeanImpl;
import edu.umn.msi.tropix.proteomics.scaffold.metadata.ScaffoldMetadata;
import edu.umn.msi.tropix.proteomics.scaffold.metadata.ScaffoldVersion;

public class ScaffoldMetadataSetter {
  public static final int DEFAULT = 3;
  private static final Log LOG = LogFactory.getLog(ScaffoldMetadataSetter.class);
  private MetadataBeanImpl<ScaffoldMetadata> metadataBean;
  private int version;

  public void setMetadataBean(final MetadataBeanImpl<ScaffoldMetadata> metadataBean) {
    this.metadataBean = metadataBean;
  }

  private void setScaffoldVersionFromInt(final int version) {
    this.version = (version == 2) ? 2 : 3;
    LOG.info("Going to set scaffold version to " + this.version);
  }

  public void setScaffoldVersion(final String versionStr) {
    try {
      setScaffoldVersionFromInt(Integer.parseInt(versionStr));
    } catch(NumberFormatException nfe) {
      LOG.warn("Failed to parse scaffold version " + versionStr + " using default " + DEFAULT);
    }
  }

  @PostConstruct
  public void init() {
    final ScaffoldMetadata metadata = new ScaffoldMetadata();
    metadata.setScaffoldVersion(version == 3 ? ScaffoldVersion.V3 : ScaffoldVersion.V2);
    metadataBean.set(metadata);
  }

}
