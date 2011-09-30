/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.proteomics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import edu.umn.msi.tropix.common.xml.XMLException;
import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.proteomics.bioml.Bioml;
import edu.umn.msi.tropix.proteomics.bioml.Note;

//Test new group without name code 
public class BiomlWriter {
  private static XMLUtility<Bioml> wrapper = new XMLUtility<Bioml>(Bioml.class);
  private final Bioml bioml;

  @SuppressWarnings("unchecked")
  public static Bioml convertMap(final Map parameterMap) {
    final Bioml bioml = new Bioml();
    final BiomlWriter biomlWriter = new BiomlWriter(bioml);

    for(final Object key : parameterMap.keySet()) {
      biomlWriter.addVariable(key.toString(), parameterMap.get(key));
    }

    return bioml;
  }

  // Map<String, String> varMap;
  private List<Note> notes;

  // Map<String, Set<String>> groupVariables;

  public BiomlWriter() {
    bioml = new Bioml();
    init(bioml);
  }

  public BiomlWriter(final Bioml bioml) {
    this.bioml = bioml;
    init(bioml);
  }

  public BiomlWriter(final Reader reader) throws XMLException {
    bioml = wrapper.deserialize(reader);
    init(bioml);
  }

  public BiomlWriter(final String filePath) throws FileNotFoundException, XMLException {
    this(new File(filePath));
  }

  public BiomlWriter(final java.io.File file) throws FileNotFoundException, XMLException {
    this(new FileReader(file));
  }

  public BiomlWriter(final InputStream stream) throws XMLException {
    this(new InputStreamReader(stream));
  }

  private void init(final Bioml bioml) {
    // this.bioml = bioml;
    // varMap = new HashMap<String, String>();
    // groupVariables = new HashMap<String, Set<String>>();

    notes = bioml.getNote();
    /*
     * for(Note note : notes) { if(! note.getType().equals("input")) { continue; } String label = note.getLabel(); String value = note.getValue(); String[] labelParts = label.split(", "); if(labelParts.length != 2) { //TODO Log continue; }
     * 
     * varMap.put(label, value);
     * 
     * String group = labelParts[0]; String name = labelParts[1]; addToGroup(group, name); } }
     */
  }

  public String toString() {
    return wrapper.toString(bioml);
  }

  public void addHeader(final String headerString) {
    final Note headerNote = new Note();
    headerNote.setType("header");
    headerNote.setLabel(headerString);
    notes.add(headerNote);
    // bioml.getNote().add(headerNote);
  }

  public void addVariable(final String varGroup, final String varName, final Object varValue) {
    final Note variableNote = new Note();
    String label;
    if(!varName.equals("")) {
      label = varGroup + ", " + varName;
    } else {
      label = varGroup;
    }
    final String value = varValue.toString();
    variableNote.setType("input");
    variableNote.setLabel(label);
    variableNote.setValue(value);

    notes.add(variableNote);
    // varMap.put(label, value);
    // addToGroup(varGroup, varName);
  }

  /*
   * private void addToGroup(String group, String varName) { if(! groupVariables.containsKey(group)) { HashSet<String> groupSet = new HashSet<String>(); groupVariables.put(group, groupSet); } groupVariables.get(group).add(varName); }
   */

  public void addVariable(final String varGroupAndName, final Object varValue) {
    final String value = varValue.toString();
    final Note variableNote = new Note();
    variableNote.setType("input");
    variableNote.setLabel(varGroupAndName);
    variableNote.setValue(value);
    notes.add(variableNote);
    // varMap.put(varGroupAndName, value);
    final String[] labelParts = varGroupAndName.split(", ");
    if(labelParts.length != 2) {
      throw new IllegalArgumentException("First argument to addVariable is not a valid ProteomicsInput label.");
    }
    // addToGroup(labelParts[0], labelParts[1]);
  }

  /*
   * public String getVariableValue(String varGroup, String varName) { if(!varName.equals("")) { return getVariableValue(varGroup + ", " + varName); } else { return getVariableValue(varGroup); } }
   * 
   * public String getVariableValue(String varGroupAndName) { return varMap.get(varGroupAndName); }
   * 
   * public Set<String> getVariables() { return varMap.keySet(); }
   * 
   * public Set<String> getVariables(String group) { return groupVariables.get(group); }
   */

}
