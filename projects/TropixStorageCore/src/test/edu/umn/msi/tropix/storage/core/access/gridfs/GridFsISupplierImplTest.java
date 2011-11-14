package edu.umn.msi.tropix.storage.core.access.gridfs;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Suppliers;
import com.mongodb.DB;

public class GridFsISupplierImplTest {
  
  @Test(groups = "unit")
  public void testGet() {
    final DB db = EasyMock.createMock(DB.class);
    final GridFsI gridFs = EasyMock.createMock(GridFsI.class);
    final Function<DB, GridFsI> factory = new Function<DB, GridFsI>() {

      public GridFsI apply(DB input) {
        assert db == input;
        return gridFs;
      }
      
    };
    final GridFsISupplierImpl gridFsISupplier = new GridFsISupplierImpl(Suppliers.ofInstance(db), factory);
    assert gridFsISupplier.get() == gridFs;
  }

}
