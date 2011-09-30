package edu.umn.msi.tropix.webgui.client.components.newwizards;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.forms.FormPanelFactory;
import edu.umn.msi.tropix.webgui.client.forms.FormPanelSupplier;
import edu.umn.msi.tropix.webgui.client.forms.ValidationListener;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;

class ScaffoldParametersPageImpl extends WizardPageImpl<VLayout> {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;
  private FormPanelSupplier formPanelSupplier;

  ScaffoldParametersPageImpl() {
    setTitle(CONSTANTS.scaffoldWizardParametersTitle());
    setDescription(CONSTANTS.scaffoldWizardParametersDescription());
    setCanvas(SmartUtils.setWidthAndHeight100(new VLayout()));
    final AsyncCallback<FormPanelSupplier> callback = new AsyncCallbackImpl<FormPanelSupplier>() {
      public void onSuccess(final FormPanelSupplier formPanelSupplierInput) {
        formPanelSupplier = formPanelSupplierInput;
        formPanelSupplier.addValidationListener(new ValidationListener() {
          public void onValidation(final boolean isValid) {
            setValid(isValid);
          }
        });
        final Widget widget = formPanelSupplier.get();
        widget.setHeight(getCanvas().getHeightAsString());
        getCanvas().addMember(widget);
      }
    };
    FormPanelFactory.createParametersPanel("scaffold", callback);
  }

  FormPanelSupplier getFormPanelSupplier() {
    return formPanelSupplier;
  }
}