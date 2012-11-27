package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Closure;

class Concurrent {

  static <T> boolean processInNThreads(final int n, final Closure<T> closure, final Iterable<T> items) {
    final ConcurrentLinkedQueue<T> workQueue = new ConcurrentLinkedQueue<T>();
    for(final T item : items) {
      workQueue.add(item);
    }
    final Runnable processRunnable = new Runnable() {
      public void run() {
        T item = workQueue.poll();
        if(item == null) {
          return;
        }
        closure.apply(item);
      }
    };
    return inNThreads(n, processRunnable);
  }

  private static boolean inNThreads(final int n, final Runnable runnable) {
    final AtomicBoolean failed = new AtomicBoolean(false);
    final Runnable wrappedRunnable = new Runnable() {

      public void run() {
        try {
          runnable.run();
        } catch(RuntimeException e) {
          failed.set(true);
          throw e;
        }
      }

    };
    final List<Thread> threadObjects = Lists.newArrayList();
    for(int i = 0; i < n; i++) {
      final Thread thread = new Thread(wrappedRunnable);
      threadObjects.add(thread);
      thread.start();
    }
    for(Thread thread : threadObjects) {
      try {
        thread.join();
      } catch(InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    return !failed.get();
  }

}
