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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class GenericDTAListImpl<S> implements DTAList {
  private final List<String> names = new LinkedList<String>();
  private final List<S> contentsList = new LinkedList<S>();

  protected void addName(final String name) {
    names.add(name);
  }

  protected void addContents(final S contents) {
    contentsList.add(contents);
  }

  public abstract S getKey(byte[] contents, String name);

  public abstract byte[] getValue(S key, String name);

  public void add(final byte[] contents, final String name) {
    contentsList.add(getKey(contents, name));
    names.add(name);
  }

  public int size() {
    return contentsList.size();
  }

  public Iterator<Entry> iterator() {
    final Iterator<Entry> iterator = new Iterator<Entry>() {
      private final Iterator<String> nameIterator = names.listIterator();
      private final Iterator<S> keyIterator = contentsList.listIterator();

      public boolean hasNext() {
        return nameIterator.hasNext();
      }

      public Entry next() {
        final String name = nameIterator.next();
        final byte[] contents = getValue(keyIterator.next(), name);

        final Entry entry = new Entry() {
          public byte[] getContents() {
            return contents;
          }

          public String getName() {
            return name;
          }
        };

        return entry;
      }

      public void remove() {
        nameIterator.remove();
        keyIterator.remove();
      }
    };
    return iterator;
  }

}
