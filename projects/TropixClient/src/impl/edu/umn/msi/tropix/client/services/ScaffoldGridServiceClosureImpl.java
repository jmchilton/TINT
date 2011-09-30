package edu.umn.msi.tropix.client.services;

import javax.xml.namespace.QName;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.proteomics.scaffold.metadata.ScaffoldMetadata;

public class ScaffoldGridServiceClosureImpl extends GridServiceModifierBase implements Closure<ScaffoldGridService> {
  private static final QName SCAFFOLD_QNAME = ScaffoldMetadata.getTypeDesc().getXmlType();

  public void apply(final ScaffoldGridService gridService) {
    final ScaffoldMetadata scaffoldMetadata =
        getMetadataResolver().getMetadata(gridService.getServiceAddress(), SCAFFOLD_QNAME, ScaffoldMetadata.class);
    gridService.setScaffoldVersion(scaffoldMetadata.getScaffoldVersion().getValue());
  }

}
