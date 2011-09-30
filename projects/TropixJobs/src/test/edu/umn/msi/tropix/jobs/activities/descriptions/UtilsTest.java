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

import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class UtilsTest {

  @Test(groups = "unit")
  public void testMerge() {
    assert Utils.merge(Lists.newArrayList("moo", "cow", "foo")).equals("moo,cow,foo");
  }

  @Test(groups = "unit")
  public void testSplit() {
    assert Iterables.elementsEqual(Utils.split("moo,cow,foo"), Lists.newArrayList("moo", "cow", "foo"));
  }

  @Test(groups = "unit")
  public void testSplitOne() {
    assert Iterables.elementsEqual(Utils.split("moo"), Lists.newArrayList("moo"));
  }

  @Test(groups = "unit")
  public void testConstructor() {
    new Utils();
  }
  
}
