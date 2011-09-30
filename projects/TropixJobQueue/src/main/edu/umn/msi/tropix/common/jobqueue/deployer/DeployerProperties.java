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

package edu.umn.msi.tropix.common.jobqueue.deployer;

import java.util.Map;

import org.springframework.util.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class DeployerProperties implements Supplier<String>, Function<String, String> {
  private final Map<String, String> properties = Maps.newLinkedHashMap();
  private Deployer deployer;

  public String get() {
    Preconditions.checkState(properties.size() == 1, "Expected to have exactly one property when get() is called, actual number " + properties.size());
    return apply(Iterables.getOnlyElement(properties.keySet()));
  }

  public String apply(final String propertyName) {
    Preconditions.checkState(properties.containsKey(propertyName));
    String propertyValue = properties.get(propertyName);
    if(!StringUtils.hasText(propertyValue) || propertyValue.startsWith("${")) {
      propertyValue = deployer.apply(propertyName);
    }
    return propertyValue;
  }

  public void setDeployer(final Deployer deployer) {
    this.deployer = deployer;
  }

  public void setProperties(final Map<String, String> properties) {
    this.properties.clear();
    this.properties.putAll(properties);
  }

}
