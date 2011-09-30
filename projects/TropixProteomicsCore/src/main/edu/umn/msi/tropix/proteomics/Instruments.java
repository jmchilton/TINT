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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class Instruments {
  private final Map<String, Map<String, Boolean>> instrumentsInfo;

  public Boolean ionUsed(final String instrument, final String ion) {
    final Map<String, Boolean> instrumentMap = instrumentsInfo.get(instrument);
    if(instrumentMap == null) {
      return null;
    }
    return instrumentMap.get(ion);
  }

  public Instruments(final String instrumentsFilePath) throws FileNotFoundException, IllegalArgumentException, IOException {
    this(new File(instrumentsFilePath));
  }

  public Instruments(final File file) throws FileNotFoundException, IllegalArgumentException, IOException {
    this(new FileReader(file));
  }

  public Instruments(final InputStream inputStream) throws IllegalArgumentException, IOException {
    this(new InputStreamReader(inputStream));
  }

  @SuppressWarnings("unchecked")
  public Instruments(final Reader reader) throws IllegalArgumentException, IOException {
    final BufferedReader bReader = new BufferedReader(reader);
    instrumentsInfo = new HashMap<String, Map<String, Boolean>>();
    String instrumentsLine; // = bReader.readLine();
    do { // Read comment lines at the beginning of the file
      instrumentsLine = bReader.readLine();
    } while(instrumentsLine.startsWith("//"));
    HashMap<String, Boolean>[] ionMaps;
    final String[] instruments = instrumentsLine.split(",");
    final int numInstruments = instruments.length - 1;
    ionMaps = (HashMap<String, Boolean>[]) Array.newInstance(HashMap.class, numInstruments);
    for(int i = 1; i <= numInstruments; i++) {
      ionMaps[i - 1] = new HashMap<String, Boolean>();
      instrumentsInfo.put(instruments[i], ionMaps[i - 1]);
    }

    while(true) {
      final String ionsLine = bReader.readLine();
      if(ionsLine == null || ionsLine.equals("")) {
        break;
      }

      final String[] ions = ionsLine.split(",");
      for(int i = 1; i < ions.length; i++) {
        ionMaps[i - 1].put(ions[0], ions[i].equals("X") ? true : false);
      }
    }

  }
}
