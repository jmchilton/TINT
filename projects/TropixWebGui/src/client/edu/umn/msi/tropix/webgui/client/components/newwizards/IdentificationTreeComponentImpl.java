package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.Collection;

import com.google.common.base.Predicate;

import edu.umn.msi.tropix.models.locations.Location;
import edu.umn.msi.tropix.models.locations.LocationPredicates;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.forms.ValidationListener;
import edu.umn.msi.tropix.webgui.client.utils.Lists;

public class IdentificationTreeComponentImpl extends LocationSelectionComponentImpl {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;
  private static final Predicate<Location> IDENTIFICATION_PREDICATE = LocationPredicates.getTropixObjectTreeItemTypePredicate(
      TropixObjectTypeEnum.PROTEIN_IDENTIFICATION_ANALYSIS, false);

  public IdentificationTreeComponentImpl(
      final TreeComponentFactory treeComponentFactory,
      final LocationFactory locationFactory,
      final Collection<TreeItem> treeItems,
      final Predicate<Location> showPredicate,
      final ValidationListener validationListener) {
    super(treeComponentFactory, locationFactory, treeItems, false, Lists.newArrayList(identificationInputType(showPredicate)),
        validationListener);
  }

  public static InputType identificationInputType(final Predicate<Location> showPredicate) {
    return new InputTypeImpl("IDENTIFICATION", TropixObjectTypeEnum.PROTEIN_IDENTIFICATION_ANALYSIS, true, true, IDENTIFICATION_PREDICATE,
        showPredicate, CONSTANTS.idWizardTreeTitle());
  }
}
