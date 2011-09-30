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

package edu.umn.msi.tropix.common.jobqueue.jobprocessors;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.DisposableResource;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.TempFileSuppliers;

public class DisposableResourceTrackerSupplierImpl implements Supplier<DisposableResourceTracker> {
  private final IOUtils ioUtils = IOUtilsFactory.getInstance();
  private final FileUtils fileUtils = FileUtilsFactory.getInstance();
  private Supplier<File> tempFileSupplier = TempFileSuppliers.getDefaultTempFileSupplier();
  private Function<File, DisposableResource> disposableResourceFactory = null;

  public DisposableResourceTracker get() {
    final DisposableResourceTrackerImpl trackerImpl = new DisposableResourceTrackerImpl();
    return trackerImpl;
  }

  public void setTempFileSupplier(final Supplier<File> tempFileSupplier) {
    this.tempFileSupplier = tempFileSupplier;
  }

  public void setDisposableResourceFactory(final Function<File, DisposableResource> disposableResourceFactory) {
    this.disposableResourceFactory = disposableResourceFactory;
  }

  class DisposableResourceTrackerImpl implements DisposableResourceTracker {
    private final List<DisposableResource> resources = new LinkedList<DisposableResource>();

    public void add(final InputStream inputStream) {
      File tempFile = null;
      try {
        tempFile = tempFileSupplier.get();
        final OutputStream outputStream = fileUtils.getFileOutputStream(tempFile);
        try {
          ioUtils.copy(inputStream, outputStream);
        } finally {
          ioUtils.closeQuietly(outputStream);
        }
        add(tempFile);
      } catch(final RuntimeException e) {
        fileUtils.deleteQuietly(tempFile);
        throw new IllegalStateException("Failed to create or write temp file for result.", e);
      }
    }

    public void add(final File file) {
      resources.add(disposableResourceFactory.apply(file));
    }

    public void add(final byte[] bytes) {
      add(new ByteArrayInputStream(bytes));
    }

    public void addCopy(final File file) {
      add(fileUtils.getFileInputStream(file));
    }

    public List<DisposableResource> getResources() {
      return resources;
    }

    public OutputStream newStream() {
      File tempFile = null;
      try {
        tempFile = tempFileSupplier.get();
        final OutputStream outputStream = fileUtils.getFileOutputStream(tempFile);
        add(tempFile);
        return outputStream;
      } catch(final RuntimeException e) {
        fileUtils.deleteQuietly(tempFile);
        throw new IllegalStateException("Failed to produce output stream", e);
      }
    }

    public void add(final InputContext inputContext) {
      inputContext.get(newStream());
    }
  }
}