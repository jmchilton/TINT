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

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.collect.StringPredicates;
import edu.umn.msi.tropix.common.io.Directory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.OutputContext;

public class FileDTAListImpl extends GenericDTAListImpl<String> {
  private Directory parentDirectory = null;

  public FileDTAListImpl(final Directory directory) {
    this.parentDirectory = directory;
  }

  public void populate() {
    populate(null);
  }

  public void populate(final String baseName) {
    final Iterable<String> dtaResources = Iterables.filter(parentDirectory.getResourceNames(null), StringPredicates.matches(".*\\.[dD][tT][aA]"));
    for(final String dtaResource : dtaResources) {
      addContents(dtaResource);
      final String fileName = dtaResource;
      if(baseName == null) {
        addName(fileName);
      } else {
        addName(baseName + fileName.substring(fileName.indexOf('.')));
      }
    }
  }

  public String getKey(final byte[] contents, final String name) {
    final OutputContext context = parentDirectory.getOutputContext(name);
    context.put(contents);
    return name;
  }

  public byte[] getValue(final String key, final String name) {
    final InputContext context = parentDirectory.getInputContext(key);
    return InputContexts.getAsByteArray(context);
  }

}
