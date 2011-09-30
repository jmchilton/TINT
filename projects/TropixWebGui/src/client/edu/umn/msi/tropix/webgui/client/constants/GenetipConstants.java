package edu.umn.msi.tropix.webgui.client.constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface GenetipConstants extends Constants {
  GenetipConstants INSTANCE = GWT.create(GenetipConstants.class);

  String newBowtieAnalysis();

  String newBowtieIndex();

}
