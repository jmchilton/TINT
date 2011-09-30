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

package edu.umn.msi.tropix.jobs.activities.descriptions;

import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class IdListTest {

  @Test(groups = "unit")
  public void testIterator() {
    final List<String> ids = Lists.newArrayList("moo", "cow", "foo");
    assert Iterables.elementsEqual(IdList.forIterable(ids), ids);
  }
  
  @Test(groups = "unit")
  public void testId() {
    final IdList list = new IdList();
    list.setId("moo");
    assert list.getId().equals("moo");
  }
  
}
