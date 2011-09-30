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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.Arrays;

/**
 * GWT Compatible utilities that would generally be available in other libraries such
 * as Google Collections and Tropix Commons.
 * 
 * @author John Chilton
 *
 */
class Utils {
  
  static List<String> split(final String str) {
    if(str.contains(",")) {
      @SuppressWarnings("unchecked")
      final List<String> splitValues = Arrays.asList(str.split(","));
      return splitValues;
    } else {
      final List<String> values = new ArrayList<String>(1);
      values.add(str);
      return values;
    }
  }

  static String merge(final Collection<String> values) {
    final StringBuilder merged = new StringBuilder();
    boolean first = true;
    for(String value : values) {
      if(!first) {
        merged.append(",");
      } else {
        first = false;
      }
      merged.append(value);
    }
    return merged.toString();
  }
    
}
