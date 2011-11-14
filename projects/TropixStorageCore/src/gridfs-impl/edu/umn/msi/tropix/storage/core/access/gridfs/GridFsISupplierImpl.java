package edu.umn.msi.tropix.storage.core.access.gridfs;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.mongodb.DB;

@Named("gridFsISupplier")
public class GridFsISupplierImpl implements Supplier<GridFsI> {
  private final Supplier<DB> dbSupplier;
  private final Function<DB, GridFsI> factory;

  @Inject
  public GridFsISupplierImpl(@Named("storageMongoDbSupplier") final Supplier<DB> dbSupplier) {
    this(dbSupplier, new GridFsIFactory());
  }

  public GridFsISupplierImpl(final Supplier<DB> dbSupplier, 
                             final Function<DB, GridFsI> factory) {
    this.dbSupplier = dbSupplier;
    this.factory = factory;
  }

  public GridFsI get() {
    return factory.apply(dbSupplier.get());
  }

}
