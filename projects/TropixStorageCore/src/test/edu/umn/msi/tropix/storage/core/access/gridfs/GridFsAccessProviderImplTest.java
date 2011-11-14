package edu.umn.msi.tropix.storage.core.access.gridfs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.input.NullInputStream;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import edu.umn.msi.tropix.common.io.OutputContexts;

public class GridFsAccessProviderImplTest {
  private GridFsI gridFsI;
  private GridFsAccessProviderImpl accessProvider;
  private String id;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    gridFsI = EasyMock.createMock(GridFsI.class);
    accessProvider = new GridFsAccessProviderImpl(Suppliers.ofInstance(gridFsI));
    id = UUID.randomUUID().toString();
  }
  
  private GridFSDBFile expectGetFile() {
    final GridFSDBFile file = EasyMock.createMock(GridFSDBFile.class);
    return expectGetFile(file);
  }

  private GridFSDBFile expectGetFile(final GridFSDBFile file) {
    EasyMock.expect(gridFsI.findOne(id)).andReturn(file);
    replayGridFsI();
    return file;
  }

  private void replayGridFsI() {
    EasyMock.replay(gridFsI);
  }

  @Test(groups = "unit")
  public void testGetLength() {
    final GridFSDBFile file = expectGetFile();
    EasyMock.expect(file.getLength()).andReturn(12345L);
    EasyMock.replay(file);
    assert accessProvider.getLength(id) == 12345L;
  }
  
  @Test(groups = "unit")
  public void testLastModified() {
    final GridFSDBFile file = expectGetFile();
    final Date date = new Date();
    EasyMock.expect(file.getUploadDate()).andReturn(date);
    EasyMock.replay(file);
    assert accessProvider.getDateModified(id) == date.getTime();
  }
  
  @Test(groups = "unit")
  public void testFileExist() {
    expectGetFile();
    assert accessProvider.fileExists(id);
  }

  @Test(groups = "unit")
  public void testFileNotExists() {
    expectGetFile(null);
    assert !accessProvider.fileExists(id);
  }
  
  @Test(groups = "unit")
  public void testDelete() {
    gridFsI.remove(id);
    replayGridFsI();
    assert accessProvider.deleteFile(id);
  }
  
  @Test(groups = "unit")
  public void testGetFile() throws IOException {
    final GridFSDBFile file = expectGetFile();
    final File destination = new File("destination"); 
    EasyMock.expect(file.writeTo(EasyMock.same(destination))).andReturn(100L);
    EasyMock.replay(file);
    accessProvider.getFile(id).get(destination);
    EasyMock.verify(file);
  }
  
  @Test(groups = "unit")
  public void testGetStream() throws IOException {
    final GridFSDBFile file = expectGetFile();
    final OutputStream outputStream = EasyMock.createMock(OutputStream.class);
    EasyMock.expect(file.writeTo(EasyMock.same(outputStream))).andReturn(123L);
    EasyMock.replay(file);
    accessProvider.getFile(id).get(outputStream);
    EasyMock.verify(file);
  }
  
  @Test(groups = "unit")
  public void testGetContext() {
    final GridFSDBFile file = expectGetFile();
    EasyMock.expect(file.getInputStream()).andReturn(new ByteArrayInputStream("Hello World!".getBytes()));
    EasyMock.replay(file);
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    accessProvider.getFile(id).get(OutputContexts.forOutputStream(outputStream));
    assert new String(outputStream.toByteArray()).equals("Hello World!");
  }
  
  @Test(groups = "unit")
  public void testCreateOutputStream() throws IOException {
    final GridFSInputFile inputFile = EasyMock.createMock(GridFSInputFile.class);
    EasyMock.expect(gridFsI.createFile(id)).andReturn(inputFile);
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    EasyMock.expect(inputFile.getOutputStream()).andReturn(byteArrayOutputStream);
    EasyMock.replay(inputFile);
    replayGridFsI();
    accessProvider.getPutFileOutputStream(id).write("Hello World!".getBytes());
    assert new String(byteArrayOutputStream.toByteArray()).equals("Hello World!");
  }
  
  @Test(groups = "unit")
  public void testCreateFileFromInput() {
    final InputStream inputStream = EasyMock.createMock(InputStream.class);
    final GridFSInputFile inputFile = EasyMock.createMock(GridFSInputFile.class);
    EasyMock.expect(gridFsI.createFile(id, inputStream)).andReturn(inputFile);
    inputFile.save();
    EasyMock.expect(inputFile.getLength()).andReturn(1235L);
    EasyMock.replay(inputFile);
    replayGridFsI();
    assert 1235L == accessProvider.putFile(id, inputStream);
    EasyMock.verify(inputFile);
  }

}
