package edu.umn.msi.tropix.client.services;

import com.google.common.base.Supplier;

public interface GridServicesSupplier<T extends GridService> extends Supplier<Iterable<T>> {

  /**
   * @return true if there are services for this grid services supplier.
   * 
   */
  boolean isEmpty();

}
