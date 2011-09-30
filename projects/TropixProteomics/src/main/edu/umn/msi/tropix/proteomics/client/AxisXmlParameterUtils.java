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

package edu.umn.msi.tropix.proteomics.client;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.grid.xml.SerializationUtils;
import edu.umn.msi.tropix.grid.xml.SerializationUtilsFactory;

class AxisXmlParameterUtils {
  private static final SerializationUtils SERIALIZATION_UTILS = SerializationUtilsFactory.getInstance();

  public static <T> T deserialize(final InputContext inputContext, final Class<T> parameterClass) {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    inputContext.get(outputStream);
    final StringReader reader = new StringReader(new String(outputStream.toByteArray()));
    final T parameters = SERIALIZATION_UTILS.deserialize(reader, parameterClass);
    return parameters;
  }

  public static <T> String serialize(final T object, final QName qName) {
    final StringWriter stringWriter = new StringWriter();
    SERIALIZATION_UTILS.serialize(stringWriter, object, qName);
    stringWriter.flush();
    return stringWriter.toString();
  }

}
