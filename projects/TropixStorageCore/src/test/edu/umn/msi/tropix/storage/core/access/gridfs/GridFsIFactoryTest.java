package edu.umn.msi.tropix.storage.core.access.gridfs;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class GridFsIFactoryTest {
  private GridFS gridFsMock;
  private GridFsI gridFs; 
  
  @BeforeMethod(groups = "unit")
  public void init() {
    gridFsMock = EasyMock.createMock(GridFS.class);
    gridFs = new GridFsIFactory().apply(gridFsMock);
  }
  
  private void replay() {
    EasyMock.replay(gridFsMock);
  }
  
  @Test(groups = "unit")
  public void testCreate() {
    final GridFSInputFile input = EasyMock.createMock(GridFSInputFile.class);
    EasyMock.expect(gridFsMock.createFile("moo")).andStubReturn(input);
    replay();
    assert gridFs.createFile("moo") == input;
  }
  
  @Test(groups = "unit")
  public void testFindOne() {
    final GridFSDBFile file = EasyMock.createMock(GridFSDBFile.class);
    EasyMock.expect(gridFsMock.findOne("moo")).andStubReturn(file);
    replay();
    assert gridFs.findOne("moo") == file;
  }
  
}
