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

package edu.umn.msi.tropix.proteomics.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

import edu.umn.msi.tropix.proteomics.DTAList;

public class ByteDTAListImpl implements DTAList {
  private final Map<String, byte[]> dtaEntries = new HashMap<String, byte[]>();

  public void add(final byte[] contents, final String name) {
    dtaEntries.put(name, contents);
  }

  public int size() {
    return dtaEntries.size();
  }

  public Iterator<Entry> iterator() {
    return Iterators.transform(dtaEntries.entrySet().iterator(), new Function<Map.Entry<String, byte[]>, Entry>() {
      public Entry apply(final java.util.Map.Entry<String, byte[]> arg0) {
        return new Entry() {
          public byte[] getContents() {
            return arg0.getValue();
          }

          public String getName() {
            return arg0.getKey();
          }
        };
      }
    });
  }
}
