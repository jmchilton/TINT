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

package edu.umn.msi.tropix.common.jobqueue.status;

import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.jobqueue.impl.StatusModifier;

public class StatusModifierManagerImplTest {

  @Test(groups = "unit")
  public void testNoModifierDoesntCauseNullPointer() {
    final StatusModifierManagerImpl manager = new StatusModifierManagerImpl();
    manager.extendStatus("moo", new Status());
  }

  class StatusModifierImpl implements StatusModifier {
    private final List<String> extendedIds = Lists.newArrayList();

    public void extendStatus(final String localJobId, final Status status) {
      extendedIds.add(localJobId);
    }

  }

  @Test(groups = "unit")
  public void testExendDelegates() {
    final StatusModifierManagerImpl manager = new StatusModifierManagerImpl();
    final StatusModifierImpl modifier = new StatusModifierImpl();
    manager.registerStatusModifier("foo", modifier);
    manager.extendStatus("foo", new Status());
    assert modifier.extendedIds.contains("foo");
  }

  @Test(groups = "unit")
  public void testUnregister() {
    final StatusModifierManagerImpl manager = new StatusModifierManagerImpl();
    final StatusModifierImpl modifier = new StatusModifierImpl();
    manager.registerStatusModifier("foo", modifier);
    manager.unregisterStatusModifier("foo", modifier);
    manager.extendStatus("foo", new Status());
    assert !modifier.extendedIds.contains("foo");
  }

}
