package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;

import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.locations.TropixObjectLocation;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItemPredicates;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.utils.Lists;

public class ScaffoldConstants {
  private static final TropixObjectType[] TYPES = new TropixObjectType[] {TropixObjectTypeEnum.FOLDER, TropixObjectTypeEnum.VIRTUAL_FOLDER,
      TropixObjectTypeEnum.PROTEIN_IDENTIFICATION_ANALYSIS};

  private static boolean validIdentification(final TreeItem treeItem, final List<IdentificationType> validAnalysisTypes) {
    if(!(treeItem instanceof TropixObjectTreeItem)) {
      return true;
    }
    final TropixObjectLocation tropixObjectTreeItem = (TropixObjectLocation) treeItem;
    final TropixObject tropixObject = tropixObjectTreeItem.getObject();
    if(!(tropixObject instanceof IdentificationAnalysis)) {
      return true;
    }
    final IdentificationAnalysis analysis = (IdentificationAnalysis) tropixObject;
    final IdentificationType analysisType = IdentificationType.fromParameterType(analysis.getIdentificationProgram());
    return validAnalysisTypes.contains(analysisType);
  }

  public static final ArrayList<IdentificationType> VALID_SCAFFOLD_IDENTIFICATION_TYPES = Lists.<IdentificationType>newArrayList(
      IdentificationType.SEQUEST, IdentificationType.XTANDEM, IdentificationType.MASCOT, IdentificationType.OMSSA);

  public static Predicate<TreeItem> validForSampleSelection(final List<IdentificationType> validAnalysisTypes) {
    final Predicate<TreeItem> typePredicate = TreeItemPredicates.getTropixObjectTreeItemTypePredicate(TYPES, true);
    return new Predicate<TreeItem>() {
      public boolean apply(final TreeItem treeItem) {
        return typePredicate.apply(treeItem) && validIdentification(treeItem, validAnalysisTypes);
      }
    };
  }

}
