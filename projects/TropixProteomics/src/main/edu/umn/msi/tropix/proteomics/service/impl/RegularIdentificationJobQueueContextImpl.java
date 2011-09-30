package edu.umn.msi.tropix.proteomics.service.impl;

import com.google.common.base.Functions;

public class RegularIdentificationJobQueueContextImpl<T> extends IdentificationJobQueueContextImpl<T, T> {

  protected RegularIdentificationJobQueueContextImpl() {
    super(Functions.<T>identity());
  }

}
