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

package edu.umn.msi.tropix.jobs.impl;

import com.google.common.base.Function;

import edu.umn.msi.tropix.models.TropixFile;

public class TropixFileFunctionImpl implements Function<String, TropixFile> {
  private final String storageServiceUrl;

  public TropixFileFunctionImpl(final String storageServiceUrl) {
    this.storageServiceUrl = storageServiceUrl;
  }

  public TropixFile apply(final String dataId) {
    final TropixFile file = new TropixFile();
    file.setStorageServiceUrl(storageServiceUrl);
    file.setFileId(dataId);
    return file;
  }
}
