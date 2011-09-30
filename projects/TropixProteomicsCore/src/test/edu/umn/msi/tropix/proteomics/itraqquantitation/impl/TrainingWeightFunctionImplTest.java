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

package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeight;
import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeights;

public class TrainingWeightFunctionImplTest {

  @Test(groups = "unit")
  public void apply() {
    final QuantificationWeight w1 = new QuantificationWeight(1.0, 1.0);
    final QuantificationWeight w2 = new QuantificationWeight(2.0, 4.0);
    QuantificationWeights weights = new QuantificationWeights(new QuantificationWeight[]{w2, w1});
    TrainingWeightFunctionImpl function = new TrainingWeightFunctionImpl(weights);
    assert function.apply(1.0).equals(1.0);
    assert function.apply(0.0).equals(1.0);
    assert function.apply(2.0).equals(4.0);
    assert function.apply(2.1).equals(4.0);
    
    weights = new QuantificationWeights(new QuantificationWeight[]{w1, w2});
    function = new TrainingWeightFunctionImpl(weights);
    assert function.apply(1.0).equals(1.0);
    assert function.apply(0.0).equals(1.0);
    assert function.apply(2.0).equals(4.0);
    assert function.apply(2.1).equals(4.0);
  }
  
  
}
