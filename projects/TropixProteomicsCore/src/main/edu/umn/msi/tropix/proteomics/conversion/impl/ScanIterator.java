package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.util.NoSuchElementException;
import java.util.Queue;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

import edu.umn.msi.tropix.proteomics.conversion.Scan;

abstract class ScanIterator extends UnmodifiableIterator<Scan> {
  private Queue<Scan> cachedScans = Lists.newLinkedList();
  protected static final Iterable<Scan> NO_MORE_SCANS = null;
  
  public boolean hasNext() {
    cacheScans();
    return !cachedScans.isEmpty();
  }

  public Scan next() {
    cacheScans();
    if(cachedScans.isEmpty()) {
      throw new NoSuchElementException();
    }
    return cachedScans.remove();
  }

  private void cacheScans() {
    if(!cachedScans.isEmpty()) {
      return;
    }
  
    final Iterable<Scan> scansToCache = getNextScans();
    if(scansToCache != NO_MORE_SCANS) {
      Iterables.addAll(cachedScans, scansToCache);
    }
  }
  
  abstract Iterable<Scan> getNextScans();

}