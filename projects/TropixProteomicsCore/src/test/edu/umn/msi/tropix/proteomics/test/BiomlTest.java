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

package edu.umn.msi.tropix.proteomics.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.BiomlWriter;
import edu.umn.msi.tropix.proteomics.bioml.Bioml;
import edu.umn.msi.tropix.proteomics.bioml.Note;

public class BiomlTest {

  @Test(groups = {"unit"})
  public void testConvertMap() {
    final Map<String, String> parameterMap = new HashMap<String, String>();
    parameterMap.put("a, param1", "value1");
    parameterMap.put("a, param2", "value2");

    final Bioml bioml = BiomlWriter.convertMap(parameterMap);

    final List<Note> notes = bioml.getNote();
    assert notes.size() == 2 : "Incorrect number of notes in converted map";
    final Note firstNote = notes.get(0);
    final Note secondNote = notes.get(1);
    assert firstNote.getType().equals("input");
    assert secondNote.getType().equals("input");
    assert firstNote.getValue().equals("value1");
    assert secondNote.getValue().equals("value2");

    // Test empty map
    final Bioml biomlEmpty = BiomlWriter.convertMap(new HashMap<String, String>());
    assert biomlEmpty.getNote().size() == 0;
  }

}
