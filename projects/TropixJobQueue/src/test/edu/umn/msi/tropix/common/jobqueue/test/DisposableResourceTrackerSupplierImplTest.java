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

package edu.umn.msi.tropix.common.jobqueue.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.DisposableResource;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.DisposableResourceTracker;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.DisposableResourceTrackerSupplierImpl;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class DisposableResourceTrackerSupplierImplTest {
  private DisposableResourceTrackerSupplierImpl supplier = null;
  private Function<File, DisposableResource> factory = null;
  private Supplier<File> fileSupplier = null;
  private DisposableResourceTracker tracker = null;

  @BeforeMethod(groups = "unit")
  public void init() {
    supplier = new DisposableResourceTrackerSupplierImpl();
    factory = EasyMockUtils.createMockFunction();
    fileSupplier = EasyMockUtils.createMockSupplier();
    supplier.setDisposableResourceFactory(factory);
    supplier.setTempFileSupplier(fileSupplier);
    tracker = supplier.get();
  }

  public void replay() {
    EasyMock.replay(factory, fileSupplier);
  }

  public void reset() {
    EasyMockUtils.verifyAndReset(factory, fileSupplier);
  }

  @Test(groups = "unit")
  public void addStream() throws IOException {
    add(AddType.STREAM);
  }

  @Test(groups = "unit")
  public void addBytes() throws IOException {
    add(AddType.BYTES);
  }

  @Test(groups = "unit")
  public void addNewStream() throws IOException {
    add(AddType.NEW_STREAM);
  }

  @Test(groups = "unit")
  public void addCopy() throws IOException {
    add(AddType.COPY);
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void invalidAddStream() throws IOException {
    invalidAdd(AddType.STREAM, ExceptionType.CREATE_FILE);
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void invalidAddBytes() throws IOException {
    invalidAdd(AddType.BYTES, ExceptionType.CREATE_FILE);
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void invalidAddNewStream() throws IOException {
    invalidAdd(AddType.NEW_STREAM, ExceptionType.CREATE_FILE);
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void invalidAddCopy() throws IOException {
    invalidAdd(AddType.COPY, ExceptionType.CREATE_FILE);
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void invalidResourceAddStream() throws IOException {
    invalidAdd(AddType.STREAM, ExceptionType.CREATE_RESOURCE);
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void invalidResourceAddBytes() throws IOException {
    invalidAdd(AddType.BYTES, ExceptionType.CREATE_RESOURCE);
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void invalidResourceAddNewStream() throws IOException {
    invalidAdd(AddType.NEW_STREAM, ExceptionType.CREATE_RESOURCE);
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void invalidResourceAddCopy() throws IOException {
    invalidAdd(AddType.COPY, ExceptionType.CREATE_RESOURCE);
  }

  enum AddType {
    STREAM, BYTES, NEW_STREAM, COPY;
  }

  enum ExceptionType {
    CREATE_FILE, CREATE_RESOURCE;
  }

  public void invalidAdd(final AddType addType, final ExceptionType exceptionType) throws IOException {
    final File tempFile = File.createTempFile("tpxtest", "");
    if(exceptionType.equals(ExceptionType.CREATE_FILE)) {
      expect(fileSupplier.get()).andThrow(new NullPointerException());
    } else if(exceptionType.equals(ExceptionType.CREATE_RESOURCE)) {
      expect(fileSupplier.get()).andReturn(tempFile);
      expect(factory.apply(tempFile)).andThrow(new IllegalArgumentException());
    }
    replay();
    final byte[] bytes = "Hello World".getBytes();
    if(addType.equals(AddType.STREAM)) {
      tracker.add(new ByteArrayInputStream(bytes));
    } else if(addType.equals(AddType.BYTES)) {
      tracker.add(bytes);
    } else if(addType.equals(AddType.NEW_STREAM)) {
      final OutputStream outputStream = tracker.newStream();
      IOUtils.copy(new ByteArrayInputStream(bytes), outputStream);
      outputStream.close();
    } else if(addType.equals(AddType.COPY)) {
      final File fileToCopy = File.createTempFile("tpxtest", "");
      FileUtils.writeStringToFile(fileToCopy, "Hello World");
      tracker.addCopy(fileToCopy);
    }
  }

  public void add(final AddType addType) throws IOException {
    final File tempFile = File.createTempFile("tpxtest", "");
    final DisposableResource disposableResource = createMock(DisposableResource.class);
    expect(fileSupplier.get()).andReturn(tempFile);
    expect(factory.apply(tempFile)).andReturn(disposableResource);
    replay();
    final byte[] bytes = "Hello World".getBytes();
    if(addType.equals(AddType.STREAM)) {
      tracker.add(new ByteArrayInputStream(bytes));
    } else if(addType.equals(AddType.BYTES)) {
      tracker.add(bytes);
    } else if(addType.equals(AddType.NEW_STREAM)) {
      final OutputStream outputStream = tracker.newStream();
      IOUtils.copy(new ByteArrayInputStream(bytes), outputStream);
      outputStream.close();
    } else if(addType.equals(AddType.COPY)) {
      final File fileToCopy = File.createTempFile("tpxtest", "");
      FileUtils.writeStringToFile(fileToCopy, "Hello World");
      tracker.addCopy(fileToCopy);
    }
    reset();
    assert FileUtils.readFileToString(tempFile).equals("Hello World");
    assert tracker.getResources().size() == 1;
    assert tracker.getResources().get(0) == disposableResource;
  }

  @Test(groups = "unit")
  public void addFilesCheckOrder() throws IOException {
    final File tempFile = File.createTempFile("tpxtest", ""), tempFile2 = File.createTempFile("tpxtest", "");
    final DisposableResource disposableResource = createMock(DisposableResource.class);
    final DisposableResource disposableResource2 = createMock(DisposableResource.class);
    expect(factory.apply(tempFile)).andReturn(disposableResource);
    expect(factory.apply(tempFile2)).andReturn(disposableResource2);
    replay();
    tracker.add(tempFile);
    tracker.add(tempFile2);
    reset();
    assert tracker.getResources().size() == 2;
    assert tracker.getResources().get(0) == disposableResource;
    assert tracker.getResources().get(1) == disposableResource2;
  }

  @Test(groups = "unit")
  public void empty() {
    assert tracker.getResources().size() == 0;
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void invalidCopy() throws IOException {
    tracker.addCopy(File.createTempFile("tpx", "test"));
  }

}
